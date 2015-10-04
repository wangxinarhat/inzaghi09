package com.milan.inzaghi09.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void show(Context context, String msg) {
		Toast.makeText(context, msg, 0).show();
	}

}
