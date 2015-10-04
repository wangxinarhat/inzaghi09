package com.milan.inzaghi09.activity;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	private long[] mHits = new long[2];
	private LayoutParams mLayoutParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	private void initUI() {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
//		1找控件
		final ImageView iv_drag = (ImageView) findViewById(R.id.iv_drag);
		final Button bt_notify_top = (Button) findViewById(R.id.bt_notify_top);
		final Button bt_notify_bottom = (Button) findViewById(R.id.bt_notify_bottom);
		
//		2获取归属地提示框保存的位置
		int locationX = SpUtil.getInt(this, ConstantValues.TOAST_ADDRESS_LOCATIONX, 0);
		int locationY = SpUtil.getInt(this, ConstantValues.TOAST_ADDRESS_LOCATIONY, 0);
		mLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = locationX;
		mLayoutParams.topMargin = locationY;
		iv_drag.setLayoutParams(mLayoutParams);
		

		if (locationY<mScreenHeight/2) {
			bt_notify_top.setVisibility(View.INVISIBLE);
			bt_notify_bottom.setVisibility(View.VISIBLE);
		} else {
			bt_notify_top.setVisibility(View.VISIBLE);
			bt_notify_bottom.setVisibility(View.INVISIBLE);

		}
		
//		4设置触摸监听事件
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
//					获取按下时候的坐标，作为初始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_MOVE:
//					获取移动过程中的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
//					移动的距离
					int disX = moveX - startX;
					int disY = moveY - startY;
					
					int left = iv_drag.getLeft()+disX;
					int top = iv_drag.getTop()+disY;
					int right = iv_drag.getRight()+disX;
					int bottom = iv_drag.getBottom()+disY;
					
					if (top<mScreenHeight/2) {
						bt_notify_top.setVisibility(View.INVISIBLE);
						bt_notify_bottom.setVisibility(View.VISIBLE);
					} else {
						bt_notify_top.setVisibility(View.VISIBLE);
						bt_notify_bottom.setVisibility(View.INVISIBLE);

					}
					
//					1容错处理
					if (left<0 || top <0 || right>mScreenWidth || bottom>mScreenHeight-22) {
						return true;
					}
//					2将移动后的坐标展示
					System.out.println(left+"----"+top+"----"+right+"----"+bottom);
					iv_drag.layout(left, top, right, bottom);
//					3重置起始坐标
					startX = (int)event.getRawX();
					startY = (int)event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
//					4保存吐司位置坐标
					SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONX, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONY, iv_drag.getTop());
					
					break;
				}
				return false;
			}
		});
		
//		5设置双击监听事件
		iv_drag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
	            mHits[mHits.length-1] = SystemClock.uptimeMillis();
	            if (mHits[mHits.length-1]-mHits[0]<500) {
	            	//满足双击事件后,调用代码
		        	int left = mScreenWidth/2 - iv_drag.getWidth()/2;
		        	int top = mScreenHeight/2 - iv_drag.getHeight()/2;
		        	int right = mScreenWidth/2+iv_drag.getWidth()/2;
		        	int bottom = mScreenHeight/2+iv_drag.getHeight()/2;
		        	
		        	//控件按以上规则显示
		        	iv_drag.layout(left, top, right, bottom);
		        	
		        	//存储最终位置
		        	SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONX, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValues.TOAST_ADDRESS_LOCATIONY, iv_drag.getTop());
				}
			}
		});
		
	}
}
