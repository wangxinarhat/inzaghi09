package com.milan.inzaghi09.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.VirusDao;
import com.milan.inzaghi09.utils.Md5Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {
	private static final int SCANNING = 100;
	protected static final int SCAN_FINISH = 101;
	private ImageView iv_sector;
	private TextView tv_scanning_name;
	private ProgressBar pb_progress;
	private TextView tv_scanning_count;
	private LinearLayout ll_add_text;
	private int index;
	private List<ScannInfo> mVirusList;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				ScannInfo info = (ScannInfo)msg.obj;
				tv_scanning_name.setText("正在扫描:"+info.name);
				TextView textView = new TextView(getApplicationContext());
				if (info.isVirus) {
					textView.setText("发现病毒:"+info.name);
					textView.setTextColor(Color.RED);
				} else {
					textView.setText("扫描正常:"+info.name);
					textView.setTextColor(Color.BLACK);
				}
				ll_add_text.addView(textView, 0);
				tv_scanning_count.setText("已扫描"+index+"个应用");
				break;
			case SCAN_FINISH:
				tv_scanning_name.setText("扫描完成");
				iv_sector.clearAnimation();
				uninstallVirus();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		initUI();
		initAnimation();
		scanningVirus();
	}

	/**
	 * 卸载病毒应用
	 */
	protected void uninstallVirus() {
		for (ScannInfo scannInfo : mVirusList) {
			String packagename = scannInfo.packagename;
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:"+packagename));
			startActivity(intent);
		}
	}

	/**
	 * 扫描病毒
	 */
	private void scanningVirus() {
		new Thread(){
			public void run() {
//				1创建记录病毒的集合，以及扫面过所有应用的集合
				List<ScannInfo> scannInfoList = new ArrayList<ScannInfo>();
				mVirusList = new ArrayList<ScannInfo>();
//				2获取所有病毒md5码集合
				List<String> virusMD5List = VirusDao.queryAllVirus(getApplicationContext());
				
//				3获取手机中所有应用签名文件，
				PackageManager pm = getPackageManager();
				List<PackageInfo> packageInfoList = pm.getInstalledPackages(
						PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
//				3.1设置进度条最大值
				pb_progress.setMax(packageInfoList.size());
//				4遍历集合，
				for (PackageInfo packageInfo : packageInfoList) {
//					5获取签名文件进行md5，
					Signature[] signatures = packageInfo.signatures;
					String signature_str = signatures[0].toCharsString();
					String signature_md5 = Md5Util.encoder(signature_str);
//					6判断是否病毒
					ScannInfo scannInfo = new ScannInfo();
					if (virusMD5List.contains(signature_md5)) {
						scannInfo.isVirus = true;
						mVirusList.add(scannInfo);
					} else {
						scannInfo.isVirus = false;
						scannInfoList.add(scannInfo);
					}
					scannInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					scannInfo.packagename = packageInfo.packageName;
					scannInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
//					7更新进度条，睡一下
					pb_progress.setProgress(++index);
					try {
						Thread.sleep(new Random().nextInt(80));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					8将正在扫描的应用发送给主线程，更新UI
					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = scannInfo;
					mHandler.sendMessage(msg);
				}
//				9扫描结束后告知主线程
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}
	class ScannInfo{
		boolean isVirus;
		String name;
		String packagename;
		Drawable icon;
	}

	/**
	 * 初始化扫描动画
	 */
	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(600);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_sector.startAnimation(rotateAnimation);
	}

	/**
	 * 找控件
	 */
	private void initUI() {
		iv_sector = (ImageView) findViewById(R.id.iv_sector);
		tv_scanning_name = (TextView) findViewById(R.id.tv_scanning_name);
		pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
		tv_scanning_count = (TextView) findViewById(R.id.tv_scanning_count);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		
		Button bt_suspend_continue = (Button) findViewById(R.id.bt_suspend_continue);
		Button bt_terminate = (Button) findViewById(R.id.bt_terminate);
	}
}
