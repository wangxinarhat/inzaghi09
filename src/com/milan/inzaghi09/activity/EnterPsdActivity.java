package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EnterPsdActivity extends Activity {
	private TextView tv_app_name;
	private ImageView iv_icon;
	private String mPackagename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd);
		initUI();
		initData();
	}

	/**
	 * 给控件填充数据
	 */
	private void initData() {
		// 1获取传递过来的包名
		
		mPackagename = getIntent().getStringExtra("packagename");
		// 2获取包管理者,获取应用信息对象
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					mPackagename, 0);
			// 3给空间赋值
			tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
			iv_icon.setBackgroundDrawable(applicationInfo.loadIcon(pm));

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		final EditText et_psd = (EditText) findViewById(R.id.et_psd);
		Button bt_submit = (Button) findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String psd = et_psd.getText().toString();
				if (TextUtils.isEmpty(psd)) {
					ToastUtil.show(getApplicationContext(), "密码不能为空");
				} else {
					if ("123".equals(psd)) {
//						密码正确，告知看门狗不要再监听
						Intent intent = new Intent("android.intent.action.SKIP");
						intent.putExtra("packagename", mPackagename);
						sendBroadcast(intent);
						finish();
					} else {
						ToastUtil.show(getApplicationContext(), "密码错误");
					}
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				finish();
			}
		});
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}
}
