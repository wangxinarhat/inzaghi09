package com.milan.inzaghi09.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;

public class SetupOverActivity extends Activity {
	private TextView tv_security_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_over);
		initUI();
		
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		String security_phone_number = SpUtil.getString(this, ConstantValues.SECURITY_PHONE_NUMBER, "");
		
		tv_security_phone.setText(security_phone_number);
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_security_phone = (TextView) findViewById(R.id.tv_security_phone);
		TextView tv_reset_security = (TextView) findViewById(R.id.tv_reset_security);
		tv_reset_security.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
