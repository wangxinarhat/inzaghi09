package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.ToastUtil;
import com.milan.inzaghi09.view.SettingItemView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_bind_sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();
		initData();
	}

	private void initData() {

		// 1获取本地存储的sim序列号，判断是否回显
		String sim_num = SpUtil.getString(this, ConstantValues.SIM_NUM, "");
		if (TextUtils.isEmpty(sim_num)) {
			siv_bind_sim.setcheck(false);
		} else {
			siv_bind_sim.setcheck(true);
		}

		// 2设置控件的点击事件，点击点击条目后将绑定状态取反
		siv_bind_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 2.1获取控件之前的选中状态
				boolean ischeck = siv_bind_sim.ischecked();
				// 2.2取反
				siv_bind_sim.setcheck(!ischeck);
				// 2.3记录sim关联状态
				if (!ischeck) {
					// 保存sim序列号
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String sim_num = manager.getSimSerialNumber();
					SpUtil.putString(getApplicationContext(),
							ConstantValues.SIM_NUM, sim_num);
				} else {
					// 删除配置文件，关于sim卡序列号的节点
					SpUtil.remove(getApplicationContext(),
							ConstantValues.SIM_NUM);
				}

			}
		});
	}

	/**
	 * 找到设置界面2的控件
	 */
	private void initUI() {
		siv_bind_sim = (SettingItemView) findViewById(R.id.siv_bind_sim);
	}

	@Override
	protected void showNextPage() {

		if (siv_bind_sim.ischecked()) {
			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(R.anim.next_in_animation,
					R.anim.next_out_animation);

		} else {
			ToastUtil.show(this, "请绑定sim卡");
		}

	}

	@Override
	protected void showPrePage() {

		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.pre_in_animation,
				R.anim.pre_out_animation);

	}
}
