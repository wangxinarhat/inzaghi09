package com.milan.inzaghi09.activity;


import com.milan.inzaghi09.R;
import com.milan.inzaghi09.engine.ProgressInfoLibrary;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

public class ProgressManagementActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_management);
		initProgressState();
	}

	/**
	 * 获取进程总数，及内存使用情况展示出来
	 */
	private void initProgressState() {
		TextView tv_total_progress = (TextView) findViewById(R.id.tv_total_progress);
		TextView tv_memory_state = (TextView) findViewById(R.id.tv_memory_state);
		
		int count = ProgressInfoLibrary.getCount(this);
		String availSpace = Formatter.formatFileSize(this, ProgressInfoLibrary.getAvailSpace(this));
		String totalSpace = Formatter.formatFileSize(this, ProgressInfoLibrary.getTotalSpace(this));
		tv_total_progress.setText("进程总数:"+count);
		tv_memory_state.setText("剩余/总共:"+availSpace+"/"+totalSpace);
	}

}
