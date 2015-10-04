package com.milan.inzaghi09.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

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
}
