package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.service.LockScreenClearService;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.ServiceUtil;
import com.milan.inzaghi09.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		initShowSystemProcess();
		initLockScreenClear();
	}

	/**
	 * 实现锁屏清理功能
	 */
	private void initLockScreenClear() {
//		1找控件
		final CheckBox cb_lock_screen_clear = (CheckBox) findViewById(R.id.cb_lock_screen_clear);
//		2回显
		boolean isrunning = ServiceUtil.isrunning(this, "com.milan.inzaghi09.service.LockScreenClearService");
		
		cb_lock_screen_clear.setChecked(isrunning);
		if (isrunning) {
			cb_lock_screen_clear.setText("锁屏清理已开启");
		} else {
			cb_lock_screen_clear.setText("锁屏清理已关闭");
		}

//		3设置点击事件
		cb_lock_screen_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_lock_screen_clear.setText("锁屏清理已开启");
					startService(new Intent(getApplicationContext(), LockScreenClearService.class));
				} else {
					cb_lock_screen_clear.setText("锁屏清理已开启");
					stopService(new Intent(getApplicationContext(), LockScreenClearService.class));
				}
			}
		});
		
	}

	/**
	 * 实现是否显示系统功能
	 */
	private void initShowSystemProcess() {
//		1找控件
		final CheckBox cb_show_system_process = (CheckBox) findViewById(R.id.cb_show_system_process);
//		2回显
		boolean isShow = SpUtil.getBoolean(this, ConstantValues.IS_SHOW_SYSTEM_PROCESS, false);
		cb_show_system_process.setChecked(isShow);
		if (isShow) {
			cb_show_system_process.setText("显示系统进程");
		} else {
			cb_show_system_process.setText("隐藏系统进程");
		}

//		3设置点击事件
		cb_show_system_process.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system_process.setText("显示系统进程");
				} else {
					cb_show_system_process.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(ProcessSettingActivity.this, ConstantValues.IS_SHOW_SYSTEM_PROCESS, isChecked);
				
			}
		});
	}
}
