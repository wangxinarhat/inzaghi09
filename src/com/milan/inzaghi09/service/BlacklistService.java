package com.milan.inzaghi09.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.milan.inzaghi09.db.dao.BlacklistDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlacklistService extends Service {

	private BlacklistDao mDao;
	private TelephonyManager mTM;
	private MyContentObserver mContentObserver;
	private InnerSmsReceiver mInnerSmsReceiver;
	private MyPhoneStateListener mPhoneStateListener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDao = BlacklistDao.getInstance(getApplicationContext());

		// 注册广播接收者，当收到短信时 拒收

		mInnerSmsReceiver = new InnerSmsReceiver();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

		registerReceiver(mInnerSmsReceiver, intentFilter);
		// 拦截电话，监听电话状态，响铃时挂断

		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();

		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);
				break;
			}
		}
	}

	class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取短信信息，如果号码在黑名单，并且拦截模式为1和3，则拦截短信
			// 1获取短信数组
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			// 2遍历数组，获取短信对象
			for (Object object : objects) {
				// 3获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 4获取短信信息
				String address = sms.getOriginatingAddress();
				// 5查询是否在黑名单
				int mode = mDao.getMode(address);

				if (mode == 1 || mode == 3) {
					abortBroadcast();
				}
			}

		}

	}

	/**
	 * 当来电号码在黑名单时挂断电话
	 * 
	 * @param incomingNumber
	 *            挂断的电话号码
	 */
	public void endCall(String incomingNumber) {
		int mode = mDao.getMode(incomingNumber);
		if (mode == 2 || mode == 3) {
			Class<?> clazz;
			try {
				// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
				clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService", String.class);
				IBinder iBinder = (IBinder) method.invoke(null,
						Context.TELEPHONY_SERVICE);

				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			mContentObserver = new MyContentObserver(new Handler(),
					incomingNumber);
			getContentResolver().registerContentObserver(
					Uri.parse("content://call_log/calls"), true,
					mContentObserver);
		}
	}

	class MyContentObserver extends ContentObserver {

		private String incomingNumber;

		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getContentResolver().delete(Uri.parse("content://call_log/calls"),
					"number=?", new String[] { incomingNumber });
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mInnerSmsReceiver != null) {
			unregisterReceiver(mInnerSmsReceiver);
		}
		if (mContentObserver != null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		if (mTM != null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}

	}

}
