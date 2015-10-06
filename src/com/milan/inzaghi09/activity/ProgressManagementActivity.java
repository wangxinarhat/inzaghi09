package com.milan.inzaghi09.activity;


import java.util.ArrayList;
import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.db.domain.ProgressInfo;
import com.milan.inzaghi09.engine.ProcessInfoProvider;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProgressManagementActivity extends Activity implements OnClickListener {
	private ArrayList<ProgressInfo> customerprogressInfoList;
	private ArrayList<ProgressInfo> systemprogressInfoList;
	private ListView lv_progress_list;
	private ProgressInfo mProgressInfo;
	private MyAdapter mAdapter;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_progress_list.setAdapter(mAdapter);
		};
	};
	private TextView tv_total_progress;
	private TextView tv_memory_state;
	private int mProgressCount;
	private String totalSpace_str;
	private long mAvailSpace;
	
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == customerprogressInfoList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}
		

		@Override
		public int getCount() {
			if (SpUtil.getBoolean(getApplicationContext(),  ConstantValues.IS_SHOW_SYSTEM_PROCESS, false)) {
				return customerprogressInfoList.size() + systemprogressInfoList.size() + 2;
			} else {
				return customerprogressInfoList.size() + 1;
			}
		}

		@Override
		public ProgressInfo getItem(int position) {
			if (position == 0 || position == customerprogressInfoList.size() + 1) {
				return null;
			}else {
				
				if (position < customerprogressInfoList.size()+1) {
					return customerprogressInfoList.get(position - 1);
				} else {
					return systemprogressInfoList.get(position - customerprogressInfoList.size() - 2);
				}
				
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (getItemViewType(position) == 0) {
//				文字标题
				ViewTitleHolder holder = new ViewTitleHolder();
				if (convertView == null) {
//				1复用convertView
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_progress_management_title_item, null);
//				2找控件
					holder.tv_progress_title = (TextView) convertView
							.findViewById(R.id.tv_progress_title);
//				3设置tag
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				
//			4给控件赋值
				if (position==0) {
					holder.tv_progress_title.setText("用户进程("+customerprogressInfoList.size()+")");
				} else {
					holder.tv_progress_title.setText("系统进程("+systemprogressInfoList.size()+")");
				}
				return convertView;
				
			} else {
				ViewHolder holder = new ViewHolder();
				if (convertView == null) {
//				1复用convertView
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_progress_management_item, null);
//				2找控件
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_progress_name = (TextView) convertView
							.findViewById(R.id.tv_progress_name);
					holder.tv_progress_space = (TextView) convertView
							.findViewById(R.id.tv_progress_space);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);
//				3设置tag
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				
//			4给控件赋值
				holder.tv_progress_name.setText(getItem(position).name);
				String size = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
				holder.tv_progress_space.setText("内存占用:"+size);
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				if (getItem(position).packageName.equals(getPackageName())) {
					holder.cb_box.setVisibility(View.GONE);
				}else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				
				holder.cb_box.setChecked(getItem(position).ischeck);
				return convertView;

			}
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_progress_name;
		TextView tv_progress_space;
		CheckBox cb_box;
	}
	static class ViewTitleHolder{
		TextView tv_progress_title;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_management);
		//按模块划分
		initProgressState();
		initProgressList();
		initButton();
	}

	/**
	 * 按钮点击功能实现
	 */
	private void initButton() {
//		1找按钮
		Button bt_select_all = (Button) findViewById(R.id.bt_select_all);
		Button bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		Button bt_clear = (Button) findViewById(R.id.bt_clear);
		Button bt_setting = (Button) findViewById(R.id.bt_setting);
//		2设置点击事件
		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;
		case R.id.bt_select_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clear();
			break;
		case R.id.bt_setting:
			setting();
			break;
		}
	}

	/**
	 * 实现设置按钮功能
	 */
	private void setting() {
//		startActivity(new Intent(this, ProcessSettingActivity.class));
		startActivityForResult(new Intent(this, ProcessSettingActivity.class), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter!=null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 实现一键清理按钮功能
	 */
	private void clear() {
		
		
		
		List<ProgressInfo> killProgressList = new ArrayList<ProgressInfo>();
//		将要杀死的进程存储在另外的集合，
		for (ProgressInfo progressInfo : customerprogressInfoList) {
			if (progressInfo.packageName.equals(getPackageName())) {
				continue;
			}
			if (progressInfo.ischeck) {
				killProgressList.add(progressInfo);
			}
			
		}
		
		for (ProgressInfo progressInfo : systemprogressInfoList) {
			if (progressInfo.ischeck) {
				killProgressList.add(progressInfo);
			}
		}

		if (killProgressList.size() == 0) {
			ToastUtil.show(this, "请选择要杀死的进程");
			return;
		} else {
			// 遍历集合,如果包含，就删除原有集合中的javabean，
			long totalReleaseSize = 0;
			for (ProgressInfo progressInfo : killProgressList) {
				if (customerprogressInfoList.contains(progressInfo)) {
					customerprogressInfoList.remove(progressInfo);
				}

				if (systemprogressInfoList.contains(progressInfo)) {
					systemprogressInfoList.remove(progressInfo);
				}
				// 杀死进程
				ProcessInfoProvider.killProgress(getApplicationContext(),
						progressInfo);
				totalReleaseSize += progressInfo.memSize;
				mAvailSpace += progressInfo.memSize;
			}
			
			// 进程总数更新
			mProgressCount -= killProgressList.size();
			tv_total_progress.setText("进程总数:"+ mProgressCount);
			// 可用剩余空间更新
			String availSpace_str = Formatter.formatFileSize(this, mAvailSpace);
			
			
			tv_memory_state.setText("剩余/总共:"+availSpace_str+"/"+totalSpace_str);
			
			// Toast提示
			ToastUtil.show(this, String.format("杀死了%d个进程，释放了%s内存空间",
					killProgressList.size(),
					Formatter.formatFileSize(this, totalReleaseSize)));
			// 更新数据适配器
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 实现反选按钮功能
	 */
	private void selectReverse() {
//		遍历集合，将所有的条目的ischeck字段置反
		for (ProgressInfo progressInfo : customerprogressInfoList) {
			if (progressInfo.packageName.equalsIgnoreCase(getPackageName())) {
				continue;
			}
			progressInfo.ischeck = !progressInfo.ischeck;
		}
		for (ProgressInfo progressInfo : systemprogressInfoList) {
			progressInfo.ischeck = !progressInfo.ischeck;
		}
		if (mAdapter!=null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 实现全选按钮功能
	 */
	private void selectAll() {
//		遍历集合，将所有的条目的ischeck字段置为true
		for (ProgressInfo progressInfo : customerprogressInfoList) {
			if (progressInfo.packageName.equalsIgnoreCase(getPackageName())) {
				continue;
			}
			progressInfo.ischeck = true;
		}
		for (ProgressInfo progressInfo : systemprogressInfoList) {
			progressInfo.ischeck = true;
		}
		
		if (mAdapter!=null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获取进程数据，展示到ListView
	 */
	private void initProgressList() {
		getData();
//		获取listview控件
		lv_progress_list = (ListView) findViewById(R.id.lv_progress_list);
//		设置条目点击事件
		final TextView tv_progress_slide_title = (TextView) findViewById(R.id.tv_progress_slide_title);
//		设置ListView滚动监听事件
		lv_progress_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (systemprogressInfoList!=null && customerprogressInfoList!=null) {
					
					if (firstVisibleItem>customerprogressInfoList.size()) {
						tv_progress_slide_title.setText("系统进程("+systemprogressInfoList.size()+")");
					} else {
						tv_progress_slide_title.setText("用户进程("+customerprogressInfoList.size()+")");
					}
				}
			}
		});
//		设置ListView条目点击事件，
		lv_progress_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == customerprogressInfoList.size() + 1) {
					return;
				} else {
//					获取点击的进程
					if (position < customerprogressInfoList.size()+1) {
						mProgressInfo = customerprogressInfoList.get(position - 1);
					} else {
						mProgressInfo = systemprogressInfoList.get(position - customerprogressInfoList.size()-2);
					}
//					点击切换选中状态
					if (mProgressInfo != null) {
						if (!mProgressInfo.packageName.equals(getPackageName())) {
							mProgressInfo.ischeck = !mProgressInfo.ischeck;
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProgressInfo.ischeck);
						} 
					}
				}
			}
		});

	
	}
	
	
	

	/**
	 * 获取进程数据集合
	 */
	private void getData() {
		new Thread() {
			public void run() {
				List<ProgressInfo> progressInfoList = ProcessInfoProvider.getProgressInfo(getApplicationContext());
				systemprogressInfoList = new ArrayList<ProgressInfo>();
				customerprogressInfoList = new ArrayList<ProgressInfo>();
				for (ProgressInfo progressInfo : progressInfoList) {
					if (progressInfo.isSystem) {
						//是系统应用
						systemprogressInfoList.add(progressInfo);
					} else {
//						是用户应用
						customerprogressInfoList.add(progressInfo);
					}
				}
				// 3通知主线程数据已准备好
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 获取进程总数，及内存使用情况展示出来
	 */
	private void initProgressState() {
		tv_total_progress = (TextView) findViewById(R.id.tv_total_progress);
		tv_memory_state = (TextView) findViewById(R.id.tv_memory_state);
		
		mProgressCount = ProcessInfoProvider.getCount(this);
		
		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		
		String availSpace_str = Formatter.formatFileSize(this, mAvailSpace);
		
		totalSpace_str = Formatter.formatFileSize(this, ProcessInfoProvider.getTotalSpace(this));
		tv_total_progress.setText("进程总数:"+mProgressCount);
		tv_memory_state.setText("剩余/总共:"+availSpace_str+"/"+totalSpace_str);
	}

	

}
