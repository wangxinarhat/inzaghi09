package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		initUI();

	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		// 0获取控件
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		// 1获取之前安全设置状态，回显
		boolean security_set_state = SpUtil.getBoolean(this,
				ConstantValues.SECURITY_SET_STATE, false);
		cb_box.setChecked(security_set_state);
		if (security_set_state) {
			cb_box.setText("防盗保护已开启");

		} else {
			cb_box.setText("防盗保护未开启");

		}

		// 2设置点击事件
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 2.1根据checkbox选中状态设置提示内容，
				if (isChecked) {
					cb_box.setText("防盗保护已开启");

				} else {
					cb_box.setText("防盗保护未开启");

				}
				// 2.2保存点击后的防盗设置状态
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValues.SECURITY_SET_STATE, isChecked);

			}
		});

	}

	@Override
	protected void showNextPage() {

		if (cb_box.isChecked()) {
			Intent intent = new Intent(this, SetupOverActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in_animation,
					R.anim.next_out_animation);

		} else {
			ToastUtil.show(this, "请开启防盗保护");
		}

	}

	@Override
	protected void showPrePage() {

		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_animation,
				R.anim.pre_out_animation);

	}
}
