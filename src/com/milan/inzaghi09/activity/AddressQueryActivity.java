package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.AddressDao;
import com.milan.inzaghi09.utils.ToastUtil;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddressQueryActivity extends Activity {
	private EditText et_phone;
	private TextView tv_query_result;
	private String mAddress;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_query);

		initUI();
	}

	private void initUI() {
		// 1获取控件
		et_phone = (EditText) findViewById(R.id.et_phone);
		Button bt_query = (Button) findViewById(R.id.bt_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		// 2设置点击事件
		bt_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3获取用户输入号码
				String phone = et_phone.getText().toString().trim();
				// 4查询,耗时操作
				if (!TextUtils.isEmpty(phone)) {
					query(phone);

					// String result =
					// AddressDao.queryAddress(getApplicationContext(),phone);
				} else {
					 Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					 et_phone.startAnimation(shake);
					ToastUtil.show(getApplicationContext(), "请输入电话号码！");
				}
			}
		});

		// 6实时查询
		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone.getText().toString().trim();
				query(phone);
			}
		});
	}

	/**
	 * 子线程中进行查询操作
	 * @param phone 要查询的电话号码
	 */
	protected void query(final String phone) {
		new Thread() {

			public void run() {
			mAddress = AddressDao.queryAddress(getApplicationContext(), phone);
			mHandler.sendEmptyMessage(0);
			};
		}.start();

	}
}
