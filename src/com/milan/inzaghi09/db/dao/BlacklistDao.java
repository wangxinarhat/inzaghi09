package com.milan.inzaghi09.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.milan.inzaghi09.db.BlacklistOpenHelper;
import com.milan.inzaghi09.db.domain.BlacklistInfo;

public class BlacklistDao {
	private BlacklistOpenHelper blacklistOpenHelper;
	private static BlacklistDao blacklistDao = null;

	// 1私有构造
	private BlacklistDao(Context context) {
		// 创建数据库及其表结构
		blacklistOpenHelper = new BlacklistOpenHelper(context);
	}

	// 2静态方法提供实例化
	public static BlacklistDao getInstance(Context context) {
		if (blacklistDao == null) {
			blacklistDao = new BlacklistDao(context);
		}
		return blacklistDao;
	}

	// 实例调用增删改查方法

	/**
	 * 向数据库中插入一条数据
	 * 
	 * @param phone
	 *            要插入的电话号码
	 * @param mode
	 *            拦截模式
	 */
	public void insert(String phone, String mode) {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2增
		ContentValues contentValues = new ContentValues();
		contentValues.put("phone", phone);
		contentValues.put("mode", mode);
		db.insert("blacklist", null, contentValues);
		db.close();
	}

	/**
	 * 操作数据库，删除指定的一条数据
	 * 
	 * @param phone
	 *            要删除的号码
	 */
	public void delete(String phone) {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2删
		db.delete("blacklist", "phone=?", new String[] { phone });
		db.close();
	}

	/**
	 * 根据电话号码更改拦截模式
	 * 
	 * @param phone
	 *            电话号码
	 * @param mode
	 *            拦截模式
	 */
	public void update(String phone, String mode) {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2改
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		db.update("blacklist", contentValues, "phone=?", new String[] { phone });
		db.close();
	}

	/**
	 * 查询数据库中所有的电话号码及对应的拦截模式至集合
	 * 
	 * @return 查询结果
	 */
	public List<BlacklistInfo> queryAll() {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2查
		Cursor cursor = db.query("blacklist", new String[] { "phone", "mode" },
				null, null, null, null, "_id desc");

		// 3封装数据到集合
		List<BlacklistInfo> list = null;
		if (cursor != null && cursor.getCount() > 0) {
			list = new ArrayList<BlacklistInfo>();
			while (cursor.moveToNext()) {
				BlacklistInfo blacklistInfo = new BlacklistInfo();
				blacklistInfo.phone = cursor.getString(0);
				blacklistInfo.mode = cursor.getString(1);
				list.add(blacklistInfo);
			}
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 从指定索引出查询固定数量数据，封装到集合
	 * 
	 * @param index
	 *            从指定的索引处开始查询
	 * @return 返回查询结果的集合，返回空表示查不到，或者异常
	 */
	public List<BlacklistInfo> queryFromIndex(int index) {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2查
		String sql = "select phone,mode from blacklist order by _id desc limit?,18;";
		Cursor cursor = db.rawQuery(sql, new String[] { index + "" });

		// 3封装数据到集合
		List<BlacklistInfo> list = null;
		if (cursor != null && cursor.getCount() > 0) {
			list = new ArrayList<BlacklistInfo>();
			while (cursor.moveToNext()) {
				BlacklistInfo blacklistInfo = new BlacklistInfo();
				blacklistInfo.phone = cursor.getString(0);
				blacklistInfo.mode = cursor.getString(1);
				list.add(blacklistInfo);
			}
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 获取数据库中数据数目
	 * 
	 * @return 返回数据库中存储数目
	 */
	public int getDatabaseCount() {
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2查
		Cursor cursor = db.rawQuery("select count(*) from blacklist;", null);
		int count = 0;
		// 3封装数据到集合
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * @param phone 电话号码
	 * @return 返回拦截模式，如果返回0表示不在黑名单
	 */
	public int getMode(String phone) {
		int mode = 0;
		// 1获取数据库操作对象
		SQLiteDatabase db = blacklistOpenHelper.getWritableDatabase();
		// 2查
		Cursor cursor = db.query("blacklist", new String[] { "mode" },
				"phone=?", new String[] { phone }, null, null, null);

		// 3封装数据到集合
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				mode = cursor.getInt(0);
			}
		}
		cursor.close();
		db.close();
		return mode;

	}
}
