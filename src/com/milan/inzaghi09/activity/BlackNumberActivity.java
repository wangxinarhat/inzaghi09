package com.milan.inzaghi09.activity;

import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.db.dao.BlacklistDao;
import com.milan.inzaghi09.db.domain.BlacklistInfo;
import com.milan.inzaghi09.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

//1复用convertView减少inflate方法调用次数，
//2使用ViewHolder减少findViewById方法调用次数
//3将ViewHolder作为静态类，减少ViewHolder对象的创建
//4listview使用分页算法，避免一次加载过多条目

public class BlackNumberActivity extends Activity {
	private BlacklistDao mDao;
	private List<BlacklistInfo> blacklist_list;
	private int mode = 1;
	private MyAdapter mAdapter;
	private int mDatabaseCount;
	private boolean mIsLoading = false;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 4构建数据适配器

			mAdapter = new MyAdapter();
			// 5将数据适配器设置给listview
			lv_black_number.setAdapter(mAdapter);
			mIsLoading = false;
		};
	};

	private ListView lv_black_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		initUI();
		initData();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return blacklist_list.size();
		}

		@Override
		public Object getItem(int position) {
			return blacklist_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				// 1将条目布局文件转换为View对象
				convertView = View.inflate(getApplicationContext(),
						R.layout.blacklist_item, null);

				// 2找子控件
				holder = new ViewHolder();
				holder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 3设置删除按钮点击事件
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 3.1数据库中删除
					mDao.delete(blacklist_list.get(position).phone);
					// 3.2集合中删除
					blacklist_list.remove(position);
					// 3.3刷新适配器
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});

			// 4给子控件设置显示内容
			BlacklistInfo blacklistInfo = blacklist_list.get(position);
			holder.tv_phone.setText(blacklistInfo.phone);

			switch (Integer.parseInt(blacklistInfo.mode)) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			return convertView;
		}

	}

	// 作为静态类，减少ViewHolder创建对象个数
	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	/**
	 * 准备数据
	 */
	private void initData() {
		// 1获取数据库操作对象
		mDao = BlacklistDao.getInstance(this);
		new Thread() {

			public void run() {
				// 2从指定索引查询数据库
				blacklist_list = mDao.queryFromIndex(0);
				mDatabaseCount = mDao.getDatabaseCount();
				// 3告知主线程数据已准备好
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		// 1找控件
		Button bt_add = (Button) findViewById(R.id.bt_add);
		lv_black_number = (ListView) findViewById(R.id.lv_black_number);
		// 2设置点击事件
		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3弹出添加黑名单号码对话框
				showAddBlackNumberDialog();
			}
		});
		// 3设置listview的滚动事件
		lv_black_number.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (blacklist_list != null) {
					// 当listview处于idle状态，最后一个条目可见，未处在正在加载的情况下再次查询数据库，更新adapter
					if (scrollState == SCROLL_STATE_IDLE && lv_black_number.getLastVisiblePosition() >= blacklist_list.size() - 1 && !mIsLoading) {
						mIsLoading = true;
						// 如果条目总数大于集合大小时才去查询数据，更新listview
						if (mDatabaseCount > blacklist_list.size()) {

							new Thread() {
								public void run() {
									// 2从istView末尾加载数据的下一条开始查询数据库
									List<BlacklistInfo> moreData = mDao.queryFromIndex(blacklist_list.size());
									mDatabaseCount = mDao.getDatabaseCount();

									// 3将查询到的结果更新的集合中
									blacklist_list.addAll(moreData);
									// 4告知主线程数据已准备好
									mHandler.sendEmptyMessage(0);

								};
							}.start();
						}

					}

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

	}

	/**
	 * 弹出添加黑名单的对话框
	 */
	protected void showAddBlackNumberDialog() {
		// 1构建dialog
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(this, R.layout.dialog_add_black_number, null);
		// 2找到子控件
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// 3监听RadioGroup条目切换
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_call:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;
				}

			}
		});
		// 4监听确定按钮
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 4.1获取用户输入的手机号码
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					// 保存到黑名单号码数据库，并同步数据适配器
					// a,将电话号码插入到数据库中
					if (mDao != null) {
						mDao.insert(phone, mode + "");
					}

					// b,将号码添加到集合，(添加到集合索引为0的位置，提高用户体验)
					BlacklistInfo blacklistInfo = new BlacklistInfo();
					blacklistInfo.phone = phone;
					blacklistInfo.mode = mode + "";

					blacklist_list.add(0, blacklistInfo);
					// c,通知数据适配器，更新
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();

				} else {
					// 提示用户电话号码不能为空
					ToastUtil.show(getApplicationContext(), "请输入要加入黑名单的电话号码");
				}
			}
		});
		// 5设置取消按钮点击事件
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// 6兼容低版本
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}
