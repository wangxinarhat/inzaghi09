package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EnterPsdActivity extends Activity {
	private TextView tv_app_name;
	private ImageView iv_icon;
	private EditText et_psd;
	private Button bt_submit;
	private Button bt_cancel;

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
//		1获取传递过来的包名
		String packagename = getIntent().getStringExtra("packagename");
//		2获取包管理者,获取应用信息对象
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
//			3给空间赋值
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
		et_psd = (EditText) findViewById(R.id.et_psd);
		bt_submit = (Button) findViewById(R.id.bt_submit);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
	}
}
