package com.milan.inzaghi09.activity;

import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.CommonNumberDao;
import com.milan.inzaghi09.engine.CommonNumberDao.Child;
import com.milan.inzaghi09.engine.CommonNumberDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroupList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number_query);
		initUI();
		initData();
	}

	/**
	 * 给ExpandableListView控件准备展示用的数据
	 */
	private void initData() {
		// 1创建数据库操作对象
		CommonNumberDao dao = new CommonNumberDao(this);
		// 2查询Group数据集合
		mGroupList = dao.getGroup(this);
		// 3构建数据适配器
		MyAdapter mAdapter = new MyAdapter();
		// 4设置数据适配器
		elv_common_number.setAdapter(mAdapter);
		// 5设置子条目点击事件
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(mGroupList.get(groupPosition).childList.get(childPosition).number);
				
				return false;
			}
		});
	}
	
	/**
	 * 调用系统应用拨打电话
	 */
	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroupList.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroupList.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroupList.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.elv_group_item, null);
			} 
			((TextView)convertView.findViewById(R.id.tv_name)).setText(getGroup(groupPosition).name);
			return convertView;
			
			
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			} 
			((TextView)convertView.findViewById(R.id.tv_name)).setText(getChild(groupPosition, childPosition).name);
			((TextView)convertView.findViewById(R.id.tv_number)).setText(getChild(groupPosition, childPosition).number);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
}
