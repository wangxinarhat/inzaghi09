package com.milan.inzaghi09.activity;

import java.util.ArrayList;
import java.util.List;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.db.domain.AppInfo;
import com.milan.inzaghi09.engine.AppInfoLibrary;
import com.milan.inzaghi09.utils.ToastUtil;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagementActivity extends Activity implements OnClickListener {
	private List<AppInfo> appInfoList = null;
	private List<AppInfo> customerAppInfoList = null;
	private List<AppInfo> systemAppInfoList = null;
	private MyAdapter mAdapter;
	private ListView lv_app_list;
	private TextView tv_app_slide_title;
	private AppInfo mAppInfo;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
//			4创建数据适配器
			mAdapter = new MyAdapter();
//			5将适配器设置给控件
			lv_app_list.setAdapter(mAdapter);
			
			tv_app_slide_title.setText("用户应用("+customerAppInfoList.size()+")");
		};
	};
	private PopupWindow mPopupWindow;
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount()+1;
		}
		
		@Override
		public int getItemViewType(int position) {
			
			if (position == 0 || position == (customerAppInfoList.size()+1)) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			return customerAppInfoList.size()+systemAppInfoList.size()+2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == (customerAppInfoList.size()+1)) {
				return null;
			} else {
				if (position<customerAppInfoList.size()+1) {
					return customerAppInfoList.get(position-1);
				} else {
					return systemAppInfoList.get(position-customerAppInfoList.size()-2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				//返回文字条目
				
				
//				1创建ViewHolder
				ViewTitleHolder holder = new ViewTitleHolder();
				if (convertView==null) {
//					2convertView复用
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_management_title_item, null);
//					3找子控件
					holder.tv_app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
//					4设置tag
					convertView.setTag(holder);
				}else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
//				5给控件赋值
				if (position == 0) {
					holder.tv_app_title.setText("用户应用("+customerAppInfoList.size()+")");
				} else {
					holder.tv_app_title.setText("系统应用("+systemAppInfoList.size()+")");
				}
				return convertView;
				
			} else {
//				返回文字加图片条目
				
//				1创建ViewHolder
				ViewHolder holder = new ViewHolder();
				if (convertView==null) {
//					2convertView复用
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_management_item, null);
//					3找子控件
					holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
					holder.tv_app_location = (TextView) convertView.findViewById(R.id.tv_app_location);
//					4设置tag
					convertView.setTag(holder);
				}else {
					holder = (ViewHolder) convertView.getTag();
				}
//				5给控件赋值
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_app_name.setText(getItem(position).name);
				if (getItem(position).isSdcardApp) {
					holder.tv_app_location.setText("sd卡应用");
				} else {
					holder.tv_app_location.setText("手机应用");
				}
				return convertView;
			}
			
			

		}
		
	}
	
	
	static class ViewTitleHolder{
		TextView tv_app_title;
	}
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_app_name;
		TextView tv_app_location;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_management);
		// 按模块划分
		initDiskState();
		initListView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	/**
	 * 初始化控件
	 */
	private void initListView() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_app_slide_title = (TextView) findViewById(R.id.tv_app_slide_title);


//		设置ListView滚动监听事件
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (systemAppInfoList!=null && customerAppInfoList!=null) {
					
					if (firstVisibleItem>customerAppInfoList.size()) {
						tv_app_slide_title.setText("系统应用("+systemAppInfoList.size()+")");
					} else {
						tv_app_slide_title.setText("用户应用("+customerAppInfoList.size()+")");
					}
				}
			}
		});
//		设置ListView条目点击事件，弹出popupwindow
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == customerAppInfoList.size() + 1) {
					return;
				} else {
					if (position < customerAppInfoList.size()+1) {
//						用户应用
						mAppInfo = customerAppInfoList.get(position - 1);
					} else {
//						系统应用
						mAppInfo = systemAppInfoList.get(position - customerAppInfoList.size()-2);
					}
					showPopupWindow(view);
				}
			}
		});

	}

	/**
	 * 弹出提示用户启动，卸载或者分享的popupwindow
	 */
	protected void showPopupWindow(View view) {
//		创建动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(600);
		alphaAnimation.setFillAfter(true);
		
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,  0.5f);
		scaleAnimation.setDuration(600);
		scaleAnimation.setFillAfter(true);
		
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		
		
//		将布局文件转换为View对象
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
//		找控件
		TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);
//		设置点击事件
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		
//		1创建窗体
		
		mPopupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
//		2指定背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
//		3指定位置
		mPopupWindow.showAsDropDown(view,view.getHeight(), -view.getHeight());
		popupView.startAnimation(animationSet);
	}

	/**
	 * 获取磁盘及sd卡存储状态,展示到控件
	 */
	private void initDiskState() {
		// 1获取路径
		String diskPath = Environment.getDataDirectory().getAbsolutePath();
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		// 2获取可用空间
		String diskAvailableSpace = Formatter.formatFileSize(this,getAvailableSpace(diskPath));
		String sdcardAvailableSpace = Formatter.formatFileSize(this,getAvailableSpace(sdcardPath));
		// 3展示到控件
		// 3.1找控件
		TextView tv_disk_available = (TextView) findViewById(R.id.tv_disk_available);
		TextView tv_sdcard_available = (TextView) findViewById(R.id.tv_sdcard_available);
		// 3.2展示
		tv_disk_available.setText("磁盘可用:" + diskAvailableSpace);
		tv_sdcard_available.setText("sd卡可用:" + sdcardAvailableSpace);
	}

	/**
	 * 获取指定路径的文件系统的可用空间大小
	 * 
	 * @param path
	 *            给定路径
	 * @return 返回可用空间大小
	 */
	private long getAvailableSpace(String path) {
		// 1构建文件系统观察对象
		StatFs statFs = new StatFs(path);
		// 2获取可用区块数目
		long availableBlocks = statFs.getAvailableBlocks();
		// 3获取区块大小
		long blockSize = statFs.getBlockSize();
		// 4返回可用空间大小
		return availableBlocks * blockSize;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			if (mAppInfo.isSystemApp) {
				ToastUtil.show(this, "系统应用不能卸载");
			} else {
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + mAppInfo.packageName));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			PackageManager pm = getPackageManager();
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.packageName);
			if (launchIntentForPackage!=null) {
				startActivity(launchIntentForPackage);
			} else {
				ToastUtil.show(this, "此应用不能被卸载");
			}
			
			break;
		case R.id.tv_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT,"告诉你一个好玩的app:"+mAppInfo.name);
			intent.setType("text/plain");
			startActivity(intent);
			
			break;
		}
		
		if (mPopupWindow!=null) {
			mPopupWindow.dismiss();
		}
	}
	/**
	 * 开启子线程获取手机应用数据
	 */
	private void getData(){
		// 2获取应用数据，封装到集合
		new Thread() {
			public void run() {

				appInfoList = AppInfoLibrary.getAppInfoList(getApplicationContext());
				//将集合根据是否系统应用拆分
				systemAppInfoList = new ArrayList<AppInfo>();
				customerAppInfoList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfoList) {
					if (appInfo.isSystemApp) {
						//是系统应用
						systemAppInfoList.add(appInfo);
					} else {
//						是用户应用
						customerAppInfoList.add(appInfo);
					}
				}
				// 3通知主线程数据已准备好
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
