package com.milan.inzaghi09.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.StreamUtil;
import com.milan.inzaghi09.utils.ToastUtil;

public class SplashActivity extends Activity {

	protected static final int UPDATE_VERSION = 100;
	protected static final int ENTER_HOME = 101;
	protected static final int URL_ERROR = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;
	private TextView tv_version_name;
	private RelativeLayout splash_rl_root;
	private int mVersionCode;
	private Context mContext;
	private String versionDes;

	private String mDdownloadUrl;
	protected String tag = "SplashActivity";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_VERSION:
				// 弹出对话框，提示用户更新
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入主界面
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(mContext, "URL异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(mContext, "IO异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(mContext, "JSON解析异常");
				enterHome();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mContext = this;

		// 1初始化ui
		initUI();
		// 2获取数据
		initData();

		// 3初始化动画
		initAnimation();

		// 4初始化数据库
		initDB("address.db");
		initDB("commonnum.db");
		initDB("antivirus.db");
		
//		5生成桌面快捷方式
		if (!SpUtil.getBoolean(this, ConstantValues.SHORTCUT_EXIST, false)) {
			initShortCut();
		}

	}

	/**
	 * 当Splash界面打开的时候，生成快捷方式
	 */
	private void initShortCut() {
//		匹配桌面应用的广播接收者action，图标，名称，以及点击快捷方式后跳转的activity(注意权限)
//		1创建意图对象
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//		2图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//		3名称
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "幸运收割者");
//		4点击快捷方式后的跳转
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		
//		5发送广播
		sendBroadcast(intent);
		SpUtil.putBoolean(this, ConstantValues.SHORTCUT_EXIST, true);
	}

	/**
	 * 将资产目录下的数据库复制到files目录
	 * 
	 * @param dbName
	 *            要拷贝的数据库名
	 */
	private void initDB(String dbName) {
		// 1创建目标文件
		File file = new File(getFilesDir(), dbName);
		if (file.exists()) {
			return;
		}

		InputStream is = null;
		FileOutputStream fos = null;
		// 2从第三方资产目录文件读取流
		try {
			is = getAssets().open(dbName);
			// 3流对接
			fos = new FileOutputStream(file);
			byte[] bys = new byte[1024];
			int len = -1;
			while ((len = is.read(bys)) != -1) {
				fos.write(bys, 0, len);
				fos.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (fos != null && is != null) {
				try {
					fos.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 添加splash界面的动画淡入效果
	 */
	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		splash_rl_root.startAnimation(alphaAnimation);
	}

	/**
	 * 提示用户更新应用
	 */
	protected void showUpdateDialog() {
		Builder builder = new AlertDialog.Builder(mContext);

		// 设置图标， 标题，内容
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("升级提醒");
		builder.setMessage(versionDes);

		// 设置按钮
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadApk();

					}
				});

		builder.setNegativeButton("稍后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						enterHome();
					}
				});

		builder.show();
	}

	/**
	 * 跳转到主界面
	 */
	protected void enterHome() {
		// 跳转到主界面的意图对象
		Intent intent = new Intent(mContext, HomeAcitivity.class);
		startActivity(intent);
		// 跳转后销毁，SplashActivity
		finish();
	}

	/**
	 * 下载apk文件
	 */
	protected void downloadApk() {
		// 判断sd卡是否挂载
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 下载的本地位置
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "inzaghi09.apk";

			HttpUtils httpUtils = new HttpUtils();

			httpUtils.download(mDdownloadUrl, path,
					new RequestCallBack<File>() {

						@Override
						public void onStart() {
							Log.i(tag, "开始下载");

							super.onStart();
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							Log.i(tag, "正在下载..........");

							Log.i(tag, "total:" + total);

							Log.i(tag, "current:" + current);

							super.onLoading(total, current, isUploading);
						}

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {

							Log.i(tag, "下载成功");

							File file = responseInfo.result;
							// 提示用户安装
							installApk(file);

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Log.i(tag, "下载失败");

						}

					});

		}

	}

	/**
	 * 提示用户安装apk文件
	 * 
	 * @param file
	 *            要安装的apk文件
	 */
	protected void installApk(File file) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");

		// startActivity(intent);

		// requestCode If >= 0, this code will be returned in onActivityResult()
		// when the activity exits.

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取数据方法
	 */
	private void initData() {
		// 1得到版本名称显示到splashactivity
		tv_version_name.setText("版本名称:" + getVersionName());
		// 2获取本地应用版本号
		mVersionCode = getVersionCode();

		Log.i(tag, "本地版本号是：" + mVersionCode);
		// 3获取服务器应用版本号
		checkVersion();

	}

	/**
	 * 获取服务器应用版本号，提示用户更新或进入主界面
	 */
	private void checkVersion() {
		new Thread() {
			public void run() {

				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				// 1请求网络
				// 1.1创建URL对象
				try {

					URL url = new URL(
							"http://10.0.2.2:8080/updateinzaghi09.json");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();

					// 1.2设置参数
					connection.setReadTimeout(2000);
					connection.setConnectTimeout(2000);
					connection.setRequestMethod("GET");
					// 1.3判断响应码
					if (connection.getResponseCode() == 200) {
						InputStream inputStream = connection.getInputStream();
						// 2解析获取的流数据
						// 2.1将流转换为字符串
						String json_str = StreamUtil.stream2String(inputStream);
						Log.i(tag, json_str);

						// 2.2解析JSON格式字符串

						JSONObject jsonObject = new JSONObject(json_str);
						String versionName = jsonObject
								.getString("versionName");
						versionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject
								.getString("versionCode");
						mDdownloadUrl = jsonObject.getString("downloadUrl");

						// 日志打印
						Log.i(tag, versionName);
						Log.i(tag, versionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDdownloadUrl);

						// 3版本比对

						if (mVersionCode < Integer.parseInt(versionCode)) {
							// 提醒用户更新版本
							msg.what = UPDATE_VERSION;
						} else {
							// 进入主界面
							msg.what = ENTER_HOME;
						}

					}

				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {

					long endTime = System.currentTimeMillis();

					// 如果请求和读取时间超过4秒则不作处理，否则强制在SplashActivity停留满4秒
					if (endTime - startTime < 4000) {

						try {
							Thread.sleep(4000 - (endTime - startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					mHandler.sendMessage(msg);
				}

			}
		}.start();
	}

	/**
	 * 获取本地应用版本号
	 * 
	 * @return 返回本地应用版本号，返回0表示异常
	 */
	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(),
					0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取清单文件中的版本名称
	 * 
	 * @return 返回应用版本名称，返回null代表异常
	 */
	private String getVersionName() {
		// 1获取包的管理者对象
		PackageManager pm = getPackageManager();
		// 2获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(),
					0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		splash_rl_root = (RelativeLayout) findViewById(R.id.splash_rl_root);
	}

}
