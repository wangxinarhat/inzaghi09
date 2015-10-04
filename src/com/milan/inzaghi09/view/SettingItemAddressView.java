package com.milan.inzaghi09.view;

import com.milan.inzaghi09.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemAddressView extends RelativeLayout {

	private TextView tv_des;
	private TextView tv_title;

	public SettingItemAddressView(Context context) {
		this(context, null);
	}

	public SettingItemAddressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemAddressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// 将设置界面的一个条目转换为view
		View view = View.inflate(context, R.layout.setting_item_address, null);
		this.addView(view);
		//找控件
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_des = (TextView) this.findViewById(R.id.tv_des);
		
	}


	/**
	 * 设置条目标题
	 * @param title 要设置的标题
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	/**设置描述内容
	 * @param des 要设置的描述内容
	 */
	public void setDes(String des){
		tv_des.setText(des);
	}
	

}
