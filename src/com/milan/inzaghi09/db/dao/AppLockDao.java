package com.milan.inzaghi09.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.milan.inzaghi09.db.AppLockOpenHelper;

public class AppLockDao {
	private AppLockOpenHelper appLockOpenHelper;
	private static AppLockDao appLockDao = null;
	private Context context;
	/**
	 * 构造方法，实例化数据库帮助类对象
	 * 
	 * @param context
	 *            上下文
	 */
	// 1私有构造
	private AppLockDao(Context context) {
		this.context = context;
		// 创建数据库及其表结构
		appLockOpenHelper = new AppLockOpenHelper(context);
	}

	// 2静态方法提供实例化
	/**
	 * 静态方法实例化数据库操作类
	 * 
	 * @param context
	 *            上下文
	 * @return 返回数据库操作类实例
	 */
	public static AppLockDao getInstance(Context context) {
		if (appLockDao == null) {
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}

	/**
	 * 向应用已加锁数据库插入一条数据
	 * 
	 * @param packagename
	 *            要加锁的应用包名
	 */
	public void insert(String packagename) {
		// 1获取数据库操作对象
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		// 2增
		ContentValues contentValues = new ContentValues();
		contentValues.put("packagename", packagename);
		db.insert("applocklist", null, contentValues);
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	/**
	 * 删除一条应用加锁数据库中的一条数据
	 * 
	 * @param packagename
	 *            要删除的应用包名
	 */
	public void delete(String packagename) {
		// 1获取数据库操作对象
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		// 2删
		db.delete("applocklist", "packagename=?", new String[] { packagename });
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	/**
	 * 查询所有已加锁应用，将包名的集合返回
	 * 
	 * @return 返回已加锁应用包名集合
	 */
	public List<String> queryAll() {
		// 1获取数据库操作对象
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		// 2查
		Cursor cursor = db.query("applocklist", new String[] { "packagename" },
				null, null, null, null, null);

		// 3封装数据到集合
		List<String> list  = new ArrayList<String>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				list.add(cursor.getString(0));
			}
		}
		cursor.close();
		db.close();
		return list;
	}

}
