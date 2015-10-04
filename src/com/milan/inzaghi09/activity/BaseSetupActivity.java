package com.milan.inzaghi09.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建手势管理对象
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e1.getX() - e2.getX() < 0) {
							// 上一页,因为父类并不知道子类具体要跳转到哪个页面，所以具体跳转实现由子类决定
							showPrePage();
						}

						if (e1.getX() - e2.getX() > 0) {
							// 下一页
							showNextPage();
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}
	
	public void nextPage(View v){
		showNextPage();
	}

	public void prePage(View v){
		showPrePage();
	}
	
	//由子类具体决定，要跳转到哪个页面
	protected abstract void showNextPage();
	protected abstract void showPrePage();

	// 监听屏幕响应事件类型
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
