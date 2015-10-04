package com.milan.inzaghi09.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

	private static SharedPreferences sp;

	/**
	 * 获取手机中更新状态
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            SharedPreferences中存储的key
	 * @param defValue
	 *            默认值
	 * @return 返回是否更新的布尔值
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {

		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}

		// 2按传进来的key取出相应的value
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 向本地存储更新状态
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            存储更新状态的key
	 * @param value
	 *            存储更新状态值
	 */
	public static void putBoolean(Context context, String key, boolean value) {

		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}

		// 2按传进来的key存储相应的value
		sp.edit().putBoolean(key, value).commit();
	}

	public static String getString(Context context, String key, String defValue) {

		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);

	}

	public static void putString(Context context, String key, String value) {

		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}

		// 2按传进来的key存储相应的value
		sp.edit().putString(key, value).commit();
	}
	

	/**
	 * 获取指定key对应的int类型的值
	 * @param context 上下文
	 * @param key 要获取值的key
	 * @param defValue 如果没有与key对应的值，获取默认值
	 * @return 返回要获取的int类型值
	 */
	public static int getInt(Context context, String key,int defValue) {

		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		// 2按传进来的key取出相应的value
		return sp.getInt(key, defValue);
	}

	/**
	 * 存储int类型的值
	 * @param context 上下文
	 * @param key 要存储值得key
	 * @param value 要存储的值
	 */
	public static void putInt(Context context, String key, int value) {

		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		// 2按传进来的key存储相应的value
		sp.edit().putInt(key, value).commit();
	}

	
	

	/**
	 * 删除配置文件中指定的节点
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            要删除节点对应的key
	 */
	public static void remove(Context context, String key) {
		// 1sp是静态变量不属于任何对象，如果存在直接使用，不存在再创建
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}

		// 2按传进来的key存储相应的value
		sp.edit().remove(key).commit();

	}

	
}
