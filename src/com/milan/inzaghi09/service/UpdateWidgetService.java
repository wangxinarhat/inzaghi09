package com.milan.inzaghi09.service;

import java.util.Timer;
import java.util.TimerTask;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.ProcessInfoProvider;
import com.milan.inzaghi09.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer mTimer;
	private InnerReceiver mInnerReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
//		定时管理总进程数，
		startTimmer();
//		注册锁屏 解锁广播接收者
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		mInnerReceiver = new InnerReceiver();
		
		registerReceiver(mInnerReceiver, intentFilter);
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				startTimmer();
			} else {
				cancelTimmer();
			}
		}
		
	}
	/**
	 * 定时刷新AppWidget
	 */
	private void startTimmer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				updateAppWidget();
			}
		}, 0, 3000);
	}
	
	/**
	 * 关闭定时刷新
	 */
	public void cancelTimmer() {
		if (mTimer!=null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	/**
	 * 填充Widget小部件
	 */
	private void updateAppWidget() {
//		1获取AppWidgetManfer对象
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
//		2将窗口部件转换为view对象
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
//		3给widget子控件设置显示内容
		remoteViews.setTextViewText(R.id.tv_process_count, "正在运行进程数:"+ProcessInfoProvider.getCount(this));
		String availMem = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+availMem);
		
		
		//点击窗体小部件,进入应用
		//1:在那个控件上响应点击事件2:延期的意图
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		//通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
		Intent broadCastintent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastintent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.bt_clear,broadcast);
		
		//窗口部件对应的广播接收者
		ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
//		更新
		aWM.updateAppWidget(componentName, remoteViews);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mInnerReceiver!=null) {
			unregisterReceiver(mInnerReceiver);
		}
		cancelTimmer();
	}

}
