package com.milan.inzaghi09.engine;

import java.util.ArrayList;
import java.util.List;

import com.milan.inzaghi09.db.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoLibrary {

	/**
	 * 获取app字段信息封装到bean对象，添加至集合返回
	 * 
	 * @param ctx
	 *            获取包管理者对象需要的上下文
	 * @return 返回封装了app字段信息bean对象的集合
	 */
	public static List<AppInfo> getAppInfoList(Context ctx) {

		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		// 1获取包管理者对象
		PackageManager pm = ctx.getPackageManager();
		// 2获取包信息集合
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		// 3遍历集合获取需要的字段信息
		for (PackageInfo packageInfo : installedPackages) {
			// 4创建appinfo对象用于封装应用信息
			AppInfo appInfo = new AppInfo();
			
			// 5获取字段信息
			// 5.1获取包名
			appInfo.packageName = packageInfo.packageName;
			// 5.2获取app信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// 5.3获取icon
			appInfo.icon = applicationInfo.loadIcon(pm);
			// 5.4获取应用名称
			appInfo.name = applicationInfo.loadLabel(pm).toString();

			// 6获取是否系统应用
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				appInfo.isSystemApp = true;
			} else {
				appInfo.isSystemApp = false;
			}
			// 7获取是否sd卡应用
			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				appInfo.isSdcardApp = true;
			} else {
				appInfo.isSdcardApp = false;
			}
			// 8将封装好的bean添加到集合
			appInfoList.add(appInfo);
		}

		return appInfoList;
	}

}
