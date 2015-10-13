package com.milan.inzaghi09.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.Md5Util;
import com.milan.inzaghi09.utils.SpUtil;
import com.milan.inzaghi09.utils.ToastUtil;

public class HomeAcitivity extends Activity {
	private GridView gv_home;
	private String[] mTitleStrs;
	private Context mContext;
	private int[] drawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;

		// 1找到控件
		initUI();
		// 2初始化数据
		initData();

	}

	/**
	 * 给主界面准备提供展示数据
	 */
	private void initData() {
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		drawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };

		// 2创建adapter
		MyAdapter myAdapter = new MyAdapter();

		// 3设置给gridView
		gv_home.setAdapter(myAdapter);

		// 4设置九宫格条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				switch (position) {
				case 0:
					// 开启对话框
					showDialog();
					break;
				case 1:
					// 通信卫士
					startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
					break;
				case 2:
					// 软件管理
					startActivity(new Intent(getApplicationContext(), AppManagementActivity.class));
					break;
				case 3:
					// 进程管理
					startActivity(new Intent(getApplicationContext(), ProgressManagementActivity.class));
					break;
				case 4:
					// 流量统计
					break;
				case 5:
					// 手机杀毒
					startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
					break;
				case 6:
					// 手机杀毒
					startActivity(new Intent(getApplicationContext(),CacheClearActivity.class));
					break;
				case 7:
					//高级工具
					startActivity(new Intent(mContext, AdvancedToolsActivity.class));
					break;
				case 8:
					Intent intent = new Intent(mContext, SettingActivity.class);
					startActivity(intent);
					break;

				}
			}
		});

	}

	/**
	 * 判断本地是否存有密码，弹出相应对话框
	 */
	protected void showDialog() {
		// 1获取本地密码
		String psd = SpUtil.getString(mContext, ConstantValues.MOBILE_SAFE_PSD,"");
		
		// 2判断本地是否有存有密码
		if (TextUtils.isEmpty(psd)) {
			// 2.1显示初始化密码对话框
			showSetPsdDialog();
		} else {
			// 2.2确认密码对话框
			showConfirmPsdDialog();
		}
	}

	/**
	 * 弹出确认密码提示框
	 */
	private void showConfirmPsdDialog() {
		// 1将确认密码的布局文件转换为view对象
		final View view = View.inflate(mContext, R.layout.dialog_confirm_psd,null);
		
		// 2弹出提示框
		Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog dialog = builder.create();
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
		
		// 3找到自定义对话框按钮
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		// 4实现确认按钮点击事件
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 4.1获取本地保存的密码（为md5码）
				String savePsd = SpUtil.getString(mContext,ConstantValues.MOBILE_SAFE_PSD, "");
				
				// 4.2获取用户输入的密码，
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				String psd = et_confirm_psd.getText().toString().trim();
				// 4.3比对密码
				if (!TextUtils.isEmpty(psd)) {
					//转为md5码与本地保存的密码比较
					String psd_md5 = Md5Util.encoder(psd);
					if (psd_md5.equals(savePsd)) {
						// 跳转
//						Intent intent = new Intent(mContext, TestActivity.class);
//						startActivity(intent);
						boolean security_set_over = SpUtil.getBoolean(mContext, ConstantValues.SECURITY_SET_OVER, false);
						if (security_set_over) {
							//跳转到安全设置功能列表界面
							Intent intent = new Intent(mContext, SetupOverActivity.class);
							startActivity(intent);
						} else {
							//跳转到安全设置导航界面1
							Intent intent = new Intent(mContext, Setup1Activity.class);
							startActivity(intent);
						}
						
						//隐藏提示框
						dialog.dismiss();

					} else {
						ToastUtil.show(mContext, "密码错误");
					}

				} else {
					ToastUtil.show(mContext, "密码不能为空");
				}

			}
		});

		// 5实现取消按钮点击事件
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 第一次进入，弹出设置密码的提示框
	 */
	private void showSetPsdDialog() {
		// 1将设置密码的布局文件转换为view对象
		final View view = View.inflate(mContext, R.layout.dialog_set_psd, null);
		
		// 2弹出提示框
		Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog dialog = builder.create();
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
		// 3找到自定义对话框按钮
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// 4实现确认按钮点击事件
		// 4.1确认按钮
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 4.2获取用户输入内容
				EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				String setPsd = et_set_psd.getText().toString().trim();
				String confirmPsd = et_confirm_psd.getText().toString().trim();
				
				// 4.3判断是否为空，比较确认
				if (!TextUtils.isEmpty(setPsd)&& !TextUtils.isEmpty(confirmPsd)) {
					// 不为空，判断是否相等
					if (setPsd.equals(confirmPsd)) {
						// MD5加密，并存储
						String setPsd_md5 = Md5Util.encoder(setPsd);										
						SpUtil.putString(mContext,ConstantValues.MOBILE_SAFE_PSD, setPsd_md5);

						// 跳转到登录成功界面
//						Intent intent = new Intent(mContext, TestActivity.class);
//						startActivity(intent);
						boolean security_set_over = SpUtil.getBoolean(mContext, ConstantValues.SECURITY_SET_OVER, false);
						if (security_set_over) {
							//跳转到安全设置功能列表界面
							Intent intent = new Intent(mContext, SetupOverActivity.class);
							startActivity(intent);
						} else {
							//跳转到安全设置导航界面1
							Intent intent = new Intent(mContext, Setup1Activity.class);
							startActivity(intent);
						}
						
						//隐藏提示框
						dialog.dismiss();

					} else {
						// 提示用户输入相等密码
						ToastUtil.show(mContext, "请确保密码一致！");
					}
				} else {
					// 提示用户输入密码
					ToastUtil.show(mContext, "密码不能为空！");
				}
			}
		});

		// 5实现取消按钮点击事件
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	/**
	 * 
	 * 创建adapter类
	 * 
	 * @author wang
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView != null) {
				view = convertView;
			} else {

				view = View.inflate(mContext, R.layout.gridview_item, null);

				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

				iv_icon.setBackgroundResource(drawableIds[position]);
				tv_title.setText(mTitleStrs[position]);

			}

			return view;
		}

	}

	/**
	 * 找到控件
	 */
	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
}
