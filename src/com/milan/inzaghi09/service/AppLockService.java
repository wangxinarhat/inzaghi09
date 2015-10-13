package com.milan.inzaghi09.service;

import java.util.List;

import com.milan.inzaghi09.activity.EnterPsdActivity;
import com.milan.inzaghi09.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AppLockService extends Service {

	private boolean mIswatch;
	private String mSkipPackagename;
	private AppLockDao mAppLockDao;
	private List<String> mLockAppList;
	private InnerReceiver mInnerReceiver;
	private MyContentObserver mContentObserver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// 1获取加锁应用包名
		
		mAppLockDao = AppLockDao.getInstance(getApplicationContext());
		
		mIswatch = true;
		watch();
		
		
		//注册广播接收者，如果收到用户已输入正确密码的应用，则跳过看门狗的监听
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SKIP");
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
		//注册内容观察者，如果应用锁数据库发生改变，重新获取加锁应用数据
		
		mContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
		super.onCreate();
	}
	
	private class MyContentObserver extends ContentObserver {

		public MyContentObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			new Thread(){
				public void run() {
					mLockAppList = mAppLockDao.queryAll();
				};
			}.start();
			super.onChange(selfChange);
		}
	}

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mSkipPackagename = intent.getStringExtra("packagename");
		}

	}

	/**
	 * 维护一个死循环，让其获取正在开启应用的包名，与加锁应用数据库中包名比对， 如果比对成功弹出拦截界面
	 */
	private void watch() {
		
		new Thread() {
			public void run() {
				mLockAppList = mAppLockDao.queryAll();
				while (mIswatch) {
					// 2获取正在开启应用的包名
					// 2.1获取ActivityManager
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					// 2.2获取正在开启应用的任务栈
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					// 2.3获取栈顶activity,获取包名
					String packagename = runningTaskInfo.topActivity.getPackageName();

					// 3如果加锁应用数据库中包含正在开启应用的包名，拦截
					if (mLockAppList.contains(packagename)) {
						if (!packagename.equals(mSkipPackagename)) {
							
							Intent intent = new Intent(getApplicationContext(),
									EnterPsdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packagename", packagename);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}
	@Override
	public void onDestroy() {
		mIswatch = false;
		if (mInnerReceiver!=null) {
			unregisterReceiver(mInnerReceiver);
		}
		if (mContentObserver!=null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		
		super.onDestroy();
	}

}
