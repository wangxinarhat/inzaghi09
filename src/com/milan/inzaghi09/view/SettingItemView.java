package com.milan.inzaghi09.view;

import com.milan.inzaghi09.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.milan.inzaghi09";
	private CheckBox cb_box;
	private TextView tv_des;
	private String tag = "SettingItemView";
	private String mDestitle;
	private String mDeson;
	private String mDesoff;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// 将设置界面的一个条目转换为view
		View view = View.inflate(context, R.layout.setting_item_view, null);
		this.addView(view);
		// 找到子控件
		TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_des = (TextView) this.findViewById(R.id.tv_des);
		cb_box = (CheckBox) this.findViewById(R.id.cb_box);
		
		// 获取自定义属性
		initAttrs(attrs);
		//将布局文件中设置的属性，获取后设置给组合控件做标题
		tv_title.setText(mDestitle);
	}

	/**
	 * @param attrs
	 *            构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
			
	}

	/**
	 * 对外提供条目选中状态,将条目选中状态与checkbox选中状态绑定
	 * 
	 * @return 返回选中checkbox状态
	 */
	public boolean ischecked() {
		return cb_box.isChecked();
	}

	/**
	 * 设置条目状态切换
	 * 
	 * @param ischeck
	 *            条目的选中状态
	 */
	public void setcheck(boolean ischeck) {
		cb_box.setChecked(ischeck);
		if (ischeck) {
			tv_des.setText(mDeson);
		} else {
			tv_des.setText(mDesoff);
		}
	}

}
