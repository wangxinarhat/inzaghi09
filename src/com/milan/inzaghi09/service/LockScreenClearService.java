package com.milan.inzaghi09.service;


import com.milan.inzaghi09.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * 
 * 杀死进程后，数据适配器未更新
 *
 */

public class LockScreenClearService extends Service {

	private InnerReceiver mInnerReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
//		1注册广播接收者，监听锁屏广播
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mInnerReceiver!=null) {
			unregisterReceiver(mInnerReceiver);
		}
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
//			2清理进程
			ProcessInfoProvider.killAll(context);
		}
		
	}

}
