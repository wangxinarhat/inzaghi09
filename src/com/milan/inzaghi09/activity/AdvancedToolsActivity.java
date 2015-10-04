package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AdvancedToolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_tools);
		//按模块划分
		initAddress();
		
	}

	private void initAddress() {
//		1找控件
		TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
//		2设置点击事件
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AddressQueryActivity.class));
			}
		});
	}
}
