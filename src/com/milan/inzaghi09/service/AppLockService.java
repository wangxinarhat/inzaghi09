package com.milan.inzaghi09.service;

import java.util.List;

import com.milan.inzaghi09.activity.EnterPsdActivity;
import com.milan.inzaghi09.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AppLockService extends Service {

	private boolean mIswatch;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mIswatch = true;
		watch();
		super.onCreate();
	}

	/**
	 * 维护一个死循环，让其获取正在开启应用的包名，与加锁应用数据库中包名比对， 如果比对成功弹出拦截界面
	 */
	private void watch() {
		
		new Thread() {
			AppLockDao appLockDao = AppLockDao.getInstance(getApplicationContext());
			List<String> lockAppList = appLockDao.queryAll();
			public void run() {
				// 1获取加锁应用包名
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
					if (lockAppList.contains(packagename)) {
						Intent intent = new Intent(getApplicationContext(),
								EnterPsdActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packagename", packagename);
						startActivity(intent);
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

}
