package com.milan.inzaghi09.service;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.AddressDao;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {
	private TelephonyManager mTM;
	private WindowManager mWM;
	private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private String mAddress;
	private TextView tv_address;
	private View mViewToast;
	private int mScreenWidth;
	private int mScreenHeight;
	private InnerOutCallReceiver mInnerOutCallReceiver;
	private MyPhoneStateListener mPhoneStateListener;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_address.setText(mAddress);
		};
	};
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
//	服务一旦开启就管理吐司显示
	@Override
	public void onCreate() {
		super.onCreate();
//		1监听电话状态
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		
//		注册拨打电话过滤条件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
//		创建广播接收者
		
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(mInnerOutCallReceiver, intentFilter);
		
	}
	
	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
//			接收到广播后，显示自定义的吐司，显示归属地号码
			String phone = getResultData();
			showToast(phone);
		}
		
	}
	
	class MyPhoneStateListener extends PhoneStateListener {
		// 2重写电话状态发生改变得时候，要完成的内容
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 2.1空闲
			case TelephonyManager.CALL_STATE_IDLE:
//				关闭吐司
				if (mWM!=null && mViewToast!=null) {
					
					mWM.removeView(mViewToast);
				}
				break;
			// 2.2摘机
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			// 2.3响铃
			case TelephonyManager.CALL_STATE_RINGING:
//				弹出吐司
				showToast(incomingNumber);
				break;
			}

		}
	}

	/**
	 * 弹出吐司显示来电号码
	 * @param incomingNumber 要显示的来电号码
	 */
	public void showToast(String incomingNumber) {
//		1布局文件转换为view对象
		mViewToast = View.inflate(this, R.layout.toast_address_view, null);
//		2找到子控件
		tv_address = (TextView) mViewToast.findViewById(R.id.tv_address);
//		3给出组合控件上归属地显示框的背景色资源
		int[] drawableIds = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,R.drawable.call_locate_green};
//		4从sp中获取归属地背景色设置
		int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_STYLE, 0);
		tv_address.setBackgroundResource(drawableIds[toastStyleIndex]);
		
		// 5封装自定义Toast需要的参数
		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 默认是可以触摸的
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");
		//6吐司所在位置
//		6.1默认指定为左上角
		params.gravity = Gravity.LEFT+Gravity.TOP;
		
//		6.2获取sp中吐司位置信息
		params.x = SpUtil.getInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONX, 0);
		params.y = SpUtil.getInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONY, 0);
		
		
//		6.3设置触摸事件,获取吐司位置信息
		mViewToast.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//获取按下坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
//					获取移动过程坐标
					int moveX = (int)event.getRawX();
					int moveY = (int)event.getRawY();
//					移动的距离
					int disX = moveX - startX;
					int disY = moveY - startY;
					
					params.x = disX + params.x;
					params.y = disY + params.y;
					
//					容错处理
					if (params.x<0) {
						params.x = 0;
					}
					if (params.y<0) {
						params.y = 0;
					}
					if (params.x>mScreenWidth-mViewToast.getWidth()) {
						params.x = mScreenWidth-mViewToast.getWidth();
					}
					if (params.y>mScreenHeight-22-mViewToast.getHeight()) {
						params.y = mScreenHeight-22-mViewToast.getHeight();
					}
//					更新位置
					mWM.updateViewLayout(mViewToast, params);
					
//					重置
					startX = params.x;
					startY = params.y;
					
					break;
				case MotionEvent.ACTION_UP:
//					保存位置
					SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONX, params.x);
					SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONY, params.y);
					break;
				}
				return true;
			}
		});
		
		
		// 7将view对象挂载到窗体显示
		mWM.addView(mViewToast, params);
//		8归属地号码查询
		query(incomingNumber);
	}

	/**
	 * 来电号码，归属地查询
	 * @param incomingNumber 要查询的号码
	 */
	private void query(final String incomingNumber) {
//		耗时操作在子线程进行
		new Thread(){

		public void run() {
			mAddress = AddressDao.queryAddress(getApplicationContext(), incomingNumber);
			mHandler.sendEmptyMessage(0);
		};}.start();
	}
	
	@Override
	public void onDestroy() {
		if (mTM!=null && mPhoneStateListener!=null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if (mInnerOutCallReceiver!=null) {
			unregisterReceiver(mInnerOutCallReceiver);
		}
		
		super.onDestroy();
	}

}
