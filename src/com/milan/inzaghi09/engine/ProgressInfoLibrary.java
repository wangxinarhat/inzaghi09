package com.milan.inzaghi09.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.db.domain.ProgressInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProgressInfoLibrary {
	/**
	 * 获取正在运行的进程总数
	 * 
	 * @param ctx
	 *            上下文
	 * @return 返回正在运行进程总数
	 */
	public static int getCount(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		return runningAppProcesses.size();
		
	}

	/**
	 * @param ctx
	 *            上下文
	 * @return 返回可用内存大小
	 */
	public static long getAvailSpace(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);

		return memoryInfo.availMem;
		
	}
	/**
	 * @param ctx
	 *            上下文
	 * @return 返回内存总大小
	 */
	public static long getTotalSpace(Context ctx) {
		
//		ActivityManager am = (ActivityManager) ctx
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo memoryInfo = new MemoryInfo();
//		am.getMemoryInfo(memoryInfo);
//		
//		return memoryInfo.totalMem;
		//内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader("proc/meminfo"));
				String line = bufferedReader.readLine();
				
				StringBuffer stringBuffer = new StringBuffer();
				char[] charArray = line.toCharArray();
				for (char c : charArray) {
					if (c >= '0' && c <= '9') {
						stringBuffer.append(c);
					}else {
						continue;
					}
				}
				String totalSpace_str = stringBuffer.toString();
				return Long.parseLong(totalSpace_str) * 1024;
				
			}catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}
	
	/**
	 * 获取正在运行进程的信息集合
	 * 
	 * @param ctx
	 *            上下文
	 * @return 返回进程信息集合
	 */
	public static List<ProgressInfo> getProgressInfo(Context ctx) {
		
		List<ProgressInfo> processInfoList = new ArrayList<ProgressInfo>();
		PackageManager pm = ctx.getPackageManager();
//		获取正在运行进程集合
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
//		遍历集合，获取字段
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProgressInfo processInfo = new ProgressInfo();
//			1获取包名
			processInfo.packageName = info.processName;
			
//			2获取进程占用空间
//			am.getMemoryInfo(new int[]{info.pid});
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(info.processName, 0);
//			3获取应用图标
				processInfo.icon = applicationInfo.loadIcon(pm);
//				4获取应用名称
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				
//				5判断是否为系统进程
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				processInfo.name = info.processName;
				processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
//			6添加到集合
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}

	/**
	 * 杀死进程
	 * @param ctx 上下文
	 * @param progressInfo 要杀死进程的javabean
	 */
	public static void killProgress(Context ctx,ProgressInfo progressInfo) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(progressInfo.packageName);
	}

}
