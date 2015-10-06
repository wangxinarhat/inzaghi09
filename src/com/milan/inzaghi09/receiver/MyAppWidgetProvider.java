package com.milan.inzaghi09.receiver;

import com.milan.inzaghi09.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyAppWidgetProvider extends AppWidgetProvider {

//	创建第一个窗体小部件，调用此方法
	@Override
	public void onEnabled(Context context) {
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onEnabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
//	创建窗体小部件时调用此方法
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
//	窗体小部件宽高发生改变时 调用此方法
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}
	
//	删除窗体小部件时调用此方法
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
//	删除最后一个窗体小部件时调用此方法
	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, UpdateWidgetService.class));
		super.onDisabled(context);
	}
}
