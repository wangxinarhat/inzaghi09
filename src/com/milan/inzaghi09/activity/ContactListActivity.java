package com.milan.inzaghi09.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.milan.inzaghi09.R;

public class ContactListActivity extends Activity {
	
	private ArrayList<HashMap<String, String>> contactList= new ArrayList<HashMap<String, String>>();
	private ListView lv_contact_list;
	private MyAdapter mAdapter;
	
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_contact_list.setAdapter(mAdapter);
			
		};
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_contactlist);
		initUI();
		
		initData();
	}

	/**
	 * 获取联系人数据,存储到list集合
	 */
	private void initData() {
		// 通过内容解析者获取系统通过内容提供者暴露的数据
		// 数据量可能较大，为耗时操作，故开启子线程获取联系人数据
		new Thread() {
			public void run() {

				// 1先查询raw_contacts表的contact_id列获取联系人id
				ContentResolver resolver = getContentResolver();
				Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				// 2清空集合数据，防止数据重复
				contactList.clear();
				// 3遍历游标，根据游标内的联系人id查询联系人其他信息
				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					Cursor dataCursor = resolver.query(Uri.parse("content://com.android.contacts/data"),
							new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{id}, null);
					// 4创建集合用于存储每个联系人数据
					HashMap<String, String> hashMap = new HashMap<String, String>();
					// 5遍历游标，获得与每个联系人id对应的数据
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						// 6判断数据类型，将对应的数据存入map
						if ("vnd.android.cursor.item/name".equals(mimetype)) {
							hashMap.put("name", data1);
						} else if ("vnd.android.cursor.item/phone_v2"
								.equals(mimetype)) {
							hashMap.put("phone", data1);
						}

					}
					dataCursor.close();
					// 7将每个联系人对应的map存入list
					contactList.add(hashMap);
					//8消息机制，告知主线程，子线程已经准备好数据集合
					mHandler.sendEmptyMessage(0);
				}
				cursor.close();
				
			};
		}.start();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// 1找到控件
		lv_contact_list = (ListView) findViewById(R.id.lv_contact_list);
		// 2设置listview条目点击事件
		lv_contact_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 3获取点击条目对应的map集合
				HashMap<String, String> item = mAdapter.getItem(position);
				// 4获取集合中的数据发送给第3个导航界面
				String phone = item.get("phone");

				// 5意图对象传递数据
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				// 点击联系人条目，获取到数据后，销毁联系人选择界面
				finish();
			}

		});
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView!=null) {
				view = convertView;
			}else {
				view = View.inflate(getApplicationContext(), R.layout.contactlist_item, null);
			//给view设置数据
				TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
				TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
				
				HashMap<String, String> item = getItem(position);
				tv_contact_name.setText(item.get("name"));
				tv_contact_phone.setText(item.get("phone"));
			}
			
			return view;
		}
		
	}
}
