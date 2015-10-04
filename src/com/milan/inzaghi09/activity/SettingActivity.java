package com.milan.inzaghi09.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.R.id;
import com.milan.inzaghi09.service.AddressService;
import com.milan.inzaghi09.service.BlacklistService;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.ServiceUtil;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.view.SettingItemAddressView;
import com.milan.inzaghi09.view.SettingItemView;

public class SettingActivity extends Activity {
	private Context mContext;
	private String[] mAaddressStyles;
	private SettingItemAddressView siav_address_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);
		mContext = this;

		// 按模块划分此界面
		initUpdate();
		initAddress();
		initToastStyle();
		initToastLocation();
		initBlacklist();
	}

	/**
	 * 黑名单拦截设置
	 */
	private void initBlacklist() {
//		1找控件
		final SettingItemView siv_blacklist = (SettingItemView) findViewById(R.id.siv_blacklist);
//		2获取黑名单拦截是否开启的状态,回显
		boolean isRunning = ServiceUtil.isrunning(this, "com.milan.inzaghi09.service.BlacklistService");
		siv_blacklist.setcheck(isRunning);
//		3设置点击事件
		siv_blacklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				4点击切换选中状态,
				boolean ischeck = siv_blacklist.ischecked();
				siv_blacklist.setcheck(!ischeck);
//				5是否开启服务
				if (!ischeck) {
					startService(new Intent(getApplicationContext(), BlacklistService.class));
				} else {
					stopService(new Intent(getApplicationContext(), BlacklistService.class));
				}
			}
		});
	}

	/**
	 * 设置归属地显示位置
	 */
	private void initToastLocation() {
//		1找控件
		SettingItemAddressView siav_address_location = (SettingItemAddressView) findViewById(R.id.siav_address_location);
//		2设置控件标题，描述内容
		siav_address_location.setTitle("归属地提示框位置");
		siav_address_location.setDes("设置归属地提示框位置");
		
//		3设置点击事件
		siav_address_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
			}
		});
	}

	/**
	 * 设置归属地显示样式
	 */
	private void initToastStyle() {
//		0准备数据
		mAaddressStyles = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色" };
		// 1找控件
		siav_address_style = (SettingItemAddressView) findViewById(R.id.siav_address_style);

		// 2设置条目标题，描述内容
		siav_address_style.setTitle("归属地显示风格");
		int toastStyleIndex = SpUtil.getInt(this, ConstantValues.TOAST_ADDRESS_STYLE, 0);
		siav_address_style.setDes(mAaddressStyles[toastStyleIndex]);

		// 3设置点击事件
		siav_address_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToastDialog();
			}

		});
	}

	/**
	 * 显示归属地样式选择对话框
	 */
	private void showToastDialog() {
//		1创建dialog构建对象
		Builder builder = new AlertDialog.Builder(this);
//		2设置dialog图标、标题
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
//		3设置点击事件
		/*
		 * 1:string类型的数组描述颜色文字数组
		 * 2:弹出对画框的时候的选中条目索引值
		 * 3:点击某一个条目后触发的点击事件
		 * */
		
		builder.setSingleChoiceItems(mAaddressStyles, 0, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				1记录选中索引值，2关闭对话框，3将选中索引值对应的描述内容显示到设置界面
				SpUtil.putInt(getApplicationContext(),ConstantValues.TOAST_ADDRESS_STYLE,which);
				dialog.dismiss();
				siav_address_style.setDes(mAaddressStyles[which]);
			}
		});
		
//		4设置取消按钮点击事件
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	/**
	 * 是否开启归属地显示
	 */
	private void initAddress() {
//		1找控件
		final SettingItemView siv_show_phone_address = (SettingItemView) findViewById(R.id.siv_show_phone_address);
		
//		2回显,获取服务是否开启(？？不从SharedPreferences中读取是否显示)
		boolean isrunning = ServiceUtil.isrunning(this,"com.milan.inzaghi09.service.AddressService");
		siv_show_phone_address.setcheck(isrunning);
		
//		3设置点击事件，切换状态
		siv_show_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				3.1获取之前的选中状态
				boolean ischeck = siv_show_phone_address.ischecked();
//				3.2状态取反,保存选择状态
				siv_show_phone_address.setcheck(!ischeck);
				SpUtil.putBoolean(getApplicationContext(), ConstantValues.SHOW_PHONE_ADDRESS, !ischeck);
//				3.3根据选中状态选择是否开启服务，管理吐司显示
				if (!ischeck) {
					startService(new Intent(getApplicationContext(), AddressService.class));
				} else {
					stopService(new Intent(getApplicationContext(), AddressService.class));
				}
			}
		});
	}

	/**
	 * 是否开启更新
	 */
	private void initUpdate() {
		// 1获取条目
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 2读取本地信息，判断更新状态,回显
		boolean isUpdate = SpUtil.getBoolean(mContext,ConstantValues.OPEN_UPDATE, false);
		siv_update.setcheck(isUpdate);

		// 3设置点击事件
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3.1获取之前的状态
				boolean ischeck = siv_update.ischecked();

				// 3.2点击条目后取反
				siv_update.setcheck(!ischeck);

				// 3.3存储用户选择状态
				SpUtil.putBoolean(mContext, ConstantValues.OPEN_UPDATE,!ischeck);
			}
		});
	}
}
