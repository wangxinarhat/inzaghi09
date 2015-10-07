package com.milan.inzaghi09.activity;

import java.util.ArrayList;
import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.db.dao.AppLockDao;
import com.milan.inzaghi09.db.domain.AppInfo;
import com.milan.inzaghi09.engine.AppInfoProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity {
	private Button bt_unlock;
	private Button bt_lock;
	private LinearLayout ll_unlock;
	private LinearLayout ll_lock;
	private TextView tv_unlock_number;
	private TextView tv_lock_number;
	private ListView lv_unlock_app_list;
	private ListView lv_lock_app_list;
	private TranslateAnimation mTranslateAnimation;
	private AppLockDao mAppLockDao;
	private MyAdapter mUnlockAdapter;
	private MyAdapter mLockAdapter;
	
	private List<AppInfo> mAppLockList;
	private List<AppInfo> mAppUnlockList;
	private Handler mHandler = new Handler(){

		public void handleMessage(android.os.Message msg) {
//			6创建数据适配器，设置给ListView
			mUnlockAdapter = new MyAdapter(false);
			lv_unlock_app_list.setAdapter(mUnlockAdapter);
			
			mLockAdapter = new MyAdapter(true);
			lv_lock_app_list.setAdapter(mLockAdapter);
		};
	};
	
	
	
	class MyAdapter extends BaseAdapter{
		private boolean islock;
		public MyAdapter(boolean islock){
			this.islock = islock;
		}

		@Override
		public int getCount() {
			if (islock) {
				tv_lock_number.setText("已加锁应用:"+mAppLockList.size());
				return mAppLockList.size();
			} else {
				tv_unlock_number.setText("未加锁应用:"+mAppUnlockList.size());
				return mAppUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (islock) {
				return mAppLockList.get(position);
			} else {
				return mAppUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.listview_app_islock_item, null);
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.iv_islock = (ImageView) convertView.findViewById(R.id.iv_islock);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
			holder.tv_name.setText(getItem(position).name);
			if (islock) {
				holder.iv_islock.setBackgroundResource(R.drawable.lock);
			} else {
				holder.iv_islock.setBackgroundResource(R.drawable.unlock);
			}
			
//			将convertView，以及getItem()赋给第三方临时变量
			final View animationView = convertView;
			final AppInfo appInfo = getItem(position);
			//			设置点击锁图标的事件
			holder.iv_islock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1执行动画
					animationView.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									// 2根据是否加锁，从相应集合中删除|增加数据
									if (islock) {
										// 2.1从锁到不锁，
										mAppLockList.remove(appInfo);
										mAppUnlockList.add(0, appInfo);
										mAppLockDao.delete(appInfo.packageName);
										mLockAdapter.notifyDataSetChanged();
										mUnlockAdapter.notifyDataSetChanged();
									} else {
										// 2.2从不锁到锁
										mAppUnlockList.remove(appInfo);
										mAppLockList.add(0, appInfo);
										mAppLockDao.insert(appInfo.packageName);
										mLockAdapter.notifyDataSetChanged();
										mUnlockAdapter.notifyDataSetChanged();
									}

								}
							});
				}
			});

			return convertView;
		}
	}
	
	static class ViewHolder{
		public ImageView iv_icon;
		public TextView tv_name;
		public ImageView iv_islock;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		initAnimation();
		initUI();
		initData();
	}

	/**
	 * 平移动画
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(300);
	}

	/**
	 * 获取数据
	 */
	private void initData() {
		new Thread() {
			public void run() {
				// 1查找所有应用，icon，name,packageName,islock
				List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				// 2查找已加锁应用
				
				mAppLockDao = AppLockDao.getInstance(getApplicationContext());
				List<String> appLockList = mAppLockDao.queryAll();
				
//				3按是否加锁，将应用分类
				mAppLockList = new ArrayList<AppInfo>();
				mAppUnlockList = new ArrayList<AppInfo>();
				
//				4遍历所有应用的集合，根据包名判断应用是否加锁，将应用分类
				for (AppInfo appInfo : appInfoList) {
//					
//					根据应用是否加锁，划分到不同的集合
					if (appLockList.contains(appInfo.packageName)) {
						mAppLockList.add(appInfo);
					} else {
						mAppUnlockList.add(appInfo);
					}
				}
				// 5通知主线程，数据已准备好
				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}
	

	/**
	 * 初始化控件
	 */
	private void initUI() {
		// 切换已加锁未加锁列表按钮
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);
		// 未加锁列表控件
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		tv_unlock_number = (TextView) findViewById(R.id.tv_unlock_number);
		lv_unlock_app_list = (ListView) findViewById(R.id.lv_unlock_app_list);
		// 已加锁列表控件
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
		tv_lock_number = (TextView) findViewById(R.id.tv_lock_number);
		lv_lock_app_list = (ListView) findViewById(R.id.lv_lock_app_list);
		
		//设置按钮点击事件
		bt_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1切换按钮背景图片
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
				// 2切换LinearLaout
				ll_unlock.setVisibility(View.VISIBLE);
				ll_lock.setVisibility(View.GONE);
			}
		});
		bt_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1切换按钮背景图片
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				// 2切换LinearLaout
				ll_unlock.setVisibility(View.GONE);
				ll_lock.setVisibility(View.VISIBLE);
			}
		});
	}
}
