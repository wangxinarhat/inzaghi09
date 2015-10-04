package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;

import android.content.Intent;
import android.os.Bundle;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	protected void showNextPage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void showPrePage() {
		//空实现
	}


}
