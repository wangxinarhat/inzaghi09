package com.milan.inzaghi09.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlacklistOpenHelper extends SQLiteOpenHelper {

	public BlacklistOpenHelper(Context context) {
		super(context, "blacklist.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacklist(_id integer primary key autoincrement,phone varchar(18),mode varchar(3));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
