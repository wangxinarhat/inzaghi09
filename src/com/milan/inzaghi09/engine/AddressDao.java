package com.milan.inzaghi09.engine;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

	/**
	 * 查询本地数据库，获得号码归属地信息
	 * 
	 * @param phone
	 *            要查询的电话号码
	 * @return 返回归属地信息
	 */
	public static String queryAddress(Context ctx, String phone) {
		String phoneAddress = "未知号码";
		// 1获取数据库操作对象
		String path = ctx.getFilesDir() + File.separator + "address.db";

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 2手机号码查询
		String regex = "^1[3-8]\\d{9}";
		if (phone.matches(regex)) {
			// 2.1获取手机号前七位
			phone = phone.substring(0, 7);
			// 2.2先查询data1表，查到即可
			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id=?", new String[] { phone }, null, null, null);
			if (cursor.moveToNext()) {
				String outkey = cursor.getString(0);

				// 2.3以data1查询到的结果作为条件查询data2表，获取号码归属地
				Cursor addressCursor = db.query("data2",
						new String[] { "location" }, "id=?",
						new String[] { outkey }, null, null, null);
				if (addressCursor.moveToNext()) {
					phoneAddress = addressCursor.getString(0);
				}
			} else {
				phoneAddress = "未知号码";
			}

		} else {
			// 3作为非手机号查询
			int length = phone.length();
			switch (length) {
			
			case 3:
				phoneAddress = "报警号码";
				break;
			
			case 5:
				phoneAddress = "服务号码";
				break;
			case 7:
				phoneAddress = "本地号码";
				break;
			case 8:
				phoneAddress = "本地号码";
				break;
			case 11:
//				3+8
				String area1 = phone.substring(1, 3);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
				if (cursor1.moveToNext()) {
					phoneAddress = cursor1.getString(0);
				} else {

					phoneAddress = "未知号码";
				}
				
				break;
			case 12:
//				4+8
				String area2 = phone.substring(1, 4);
				Cursor cursor2 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area2}, null, null, null);
				if (cursor2.moveToNext()) {
					phoneAddress = cursor2.getString(0);
				} else {

					phoneAddress = "未知号码";
				}
				break;

			
			}

		}

		return phoneAddress;

	}

}
