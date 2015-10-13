package com.milan.inzaghi09.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.milan.inzaghi09.R;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	private static final int SCANNING = 100;
	private static final int SCAN_FINISH = 101;
	protected static final int UPDATE_CACHE_APP = 102;
	private ProgressBar pb_progress;
	private TextView tv_scanning_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int index;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				tv_scanning_name.setText("正在扫描:"+(String)msg.obj);
				break;
			case UPDATE_CACHE_APP:
//				1将条目布局文件转换为View对象
				View view = View.inflate(CacheClearActivity.this, R.layout.cache_clear_item, null);
//				2找子控件
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
				ImageView iv_single_clear = (ImageView) view.findViewById(R.id.iv_single_clear);
//				3填充控件
				CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_name.setText(cacheInfo.name);
				System.out.println("显示结果");
				String cacheSize_str = Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize);
				tv_cache_size.setText("缓存:"+cacheSize_str);
//				4将填充好的条目控件添加至LinearLayout
				ll_add_text.addView(view, 0);
				break;
			case SCAN_FINISH:
				tv_scanning_name.setText("扫描完成!");
				break;
			}
		};
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		scanningCache();
	
	}

	/**
	 * 扫描手机中缓存
	 */
	private void scanningCache() {
		new Thread(){
			public void run() {
				// 1获取手机中所有应用的包名
				mPm = getPackageManager();
				List<PackageInfo> packageInfoList = mPm.getInstalledPackages(0);
				// 设置进度条最大值
				pb_progress.setMax(packageInfoList.size());
				
				// 2遍历集合，根据包名，反射调用获取缓存的方法
				for (PackageInfo packageInfo : packageInfoList) {
					// 3更新进度条，睡一下
					pb_progress.setProgress(++index);
					try {
						Thread.sleep(new Random().nextInt(80));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// 4获取缓存对象
					getCache(packageInfo.packageName);

					// 5将正在扫描的应用发送给主线程，更新UI
					String name = null;
					try {
						name = mPm.getApplicationInfo(packageInfo.packageName, 0).loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = name;
					mHandler.sendMessage(msg);
				}
				// 6扫描结束后告知主线程
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
				
			};
		}.start();
	}
	/**
	 * 根据包名获取应用缓存
	 * @param packageName 包名
	 */
	private void getCache(String packageName) {
		try {
			// 1获取字节码文件对象
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 2获取方法对象					
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,int.class,IPackageStatsObserver.Stub.class);
			// 3构建IPackageStatsObserver.Stub对象
			IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
				public void onGetStatsCompleted(PackageStats stats,
						boolean succeeded) {
					long cacheSize = stats.cacheSize;
					// 如果应用有缓存，维护一个缓存信息对象，并发送消息给主线程更新UI
					if (cacheSize > 0) {
						CacheInfo cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packagename = stats.packageName;
						try {
							cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
							cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
						
						Message msg = Message.obtain();
						msg.what = UPDATE_CACHE_APP;
						msg.obj = cacheInfo;
						mHandler.sendMessage(msg);
						System.out.println("发送清理过程消息");
					}
				}
			};
//			4调用隐藏的方法
			method.invoke(mPm, packageName,-1, mStatsObserver);
//			getPackageSizeInfo(packageName, UserHandle.myUserId(), observer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class CacheInfo {
		long cacheSize;
		String name;
		String packagename;
		Drawable icon;
	}

	/**
	 * 找控件
	 */
	private void initUI() {
		Button bt_clear = (Button) findViewById(R.id.bt_clear);
		pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
		tv_scanning_name = (TextView) findViewById(R.id.tv_scanning_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}
}
