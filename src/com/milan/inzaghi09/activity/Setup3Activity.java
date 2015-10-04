package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone_num;
	private Button bt_select_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();
		initData();
	}

	private void initData() {
		// 1获取本地安全联系人号码，回显
		String security_phone_number = SpUtil.getString(this,
				ConstantValues.SECURITY_PHONE_NUMBER, "");
		if (TextUtils.isEmpty(security_phone_number)) {
			et_phone_num.setText("");
		} else {
			et_phone_num.setText(security_phone_number);
		}

		// 2设置点击事件，从联系人中选择安全号码
		bt_select_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			// 将联系人界面传递的结果，取出并赋给EditText
			String phone = data.getStringExtra("phone");

			phone = phone.replace("-", "").replace(" ", "").trim();
			et_phone_num.setText(phone);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		et_phone_num = (EditText) findViewById(R.id.et_phone_num);
		bt_select_contact = (Button) findViewById(R.id.bt_select_contact);

	}

	@Override
	protected void showNextPage() {

		String phone = et_phone_num.getText().toString().trim();

		if (TextUtils.isEmpty(phone)) {
			ToastUtil.show(this, "请选择安全联系人号码");
		} else {
			SpUtil.putString(this, ConstantValues.SECURITY_PHONE_NUMBER, phone);

			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(R.anim.next_in_animation,
					R.anim.next_out_animation);
		}

	}

	@Override
	protected void showPrePage() {

		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_animation,
				R.anim.pre_out_animation);

	}
}
