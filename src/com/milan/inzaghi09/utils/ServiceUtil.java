package com.milan.inzaghi09.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	private static ActivityManager mAM;

	/**
	 * 获取指定服务是否开启
	 * 
	 * @param ctx
	 *            上下文
	 * @param serviceName
	 *            服务名
	 * @return 返回服务是否开启
	 */
	public static boolean isrunning(Context ctx, String serviceName) {
		// 1获取activity管理对象
		mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

		// 2获取在运行服务的list集合
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(188);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// 3判断要查找的服务是否在list集合
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}

		return false;

	}

}
