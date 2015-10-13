package com.milan.inzaghi09.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {

	/**
	 * 查询病毒数据库，获取所有病毒md5码的集合
	 * @param ctx 上下文
	 * @return 返回病毒数据库中md5码的集合
	 */
	public static List<String> queryAllVirus(Context ctx) {
		List<String> virusMD5List = new ArrayList<String>();
		// 1获取数据库操作对象
		String path = ctx.getFilesDir() + File.separator + "antivirus.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
		// 2查询数据库中所用病毒MD5码的集合
		Cursor cursor = db.query("datable", new String[]{"md5"}, 
				null, null, null, null, null);
		if (cursor!=null && cursor.getCount()>0) {
			while (cursor.moveToNext()) {
				String md5 = cursor.getString(0);
				virusMD5List.add(md5);
			}
		}
		return virusMD5List;
	}
}
