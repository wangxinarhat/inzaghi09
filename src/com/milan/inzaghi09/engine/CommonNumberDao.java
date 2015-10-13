package com.milan.inzaghi09.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	public CommonNumberDao(Context context){
		this.context = context;
	}
	private Context context;
	
	private String path = context.getFilesDir() + File.separator + "commonnum.db";
	/**
	 * 查询常用号码的Group
	 * @param ctx 上下文
	 * @return 返回封装了Group的集合
	 */
	public List<Group> getGroup(Context ctx) {
		// 1获取数据库操作对象
		SQLiteDatabase  db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
		// 2查询
		List<Group> groupList = new ArrayList<Group>();
		Cursor cusor = db.query("classlist", new String[]{"name","idx"}, null, null, null, null, null);
		while (cusor.moveToNext()) {
			Group group = new Group();
			group.name = cusor.getString(0);
			group.idx = cusor.getString(1);
			group.childList = getChild(group.idx);
			groupList.add(group);
		}
		
		cusor.close();
		db.close();
		return groupList;
	}
	
	/**查询常用号码的Child
	 * @param idx 常用号码classlist表的idx列
	 * @return 返回封装了Child的集合
	 */
	public List<Child> getChild(String idx) {
		// 1数据库操作对象要重新获取，因为在getGroup方法中关闭了数据库，如果复用会导致异常
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 2查询
		List<Child> childList = new ArrayList<Child>();
		Cursor cusor = db.query("table"+idx, new String[]{"_id","number","name"}, null, null, null, null, null);
		while (cusor.moveToNext()) {
			Child child = new Child();
			child._id = cusor.getString(0);
			child.number = cusor.getString(1);
			child.name =  cusor.getString(2);
			
			childList.add(child);
		}
		
		cusor.close();
		db.close();
		return childList;
	}
	
	public class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}
	public class Child{
		public String _id;
		public String number;
		public String name;
	}

}
