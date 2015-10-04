package com.milan.inzaghi09.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
	
	
	@Override
	public void onCreate() {
		super.onCreate();
//		1获取位置管理者对象
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
//		2以最优方式定位criteria
		
		Criteria criteria = new Criteria();
		
		criteria.setCostAllowed(true);
		
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		String provider = manager.getBestProvider(criteria, true);
		
//		3更新坐标
		
		manager.requestLocationUpdates(provider, 8, 6.6f, new MyLocationListener());
		
		
		
	}
	
	
	public class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
//			经纬度
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			
//			当位置改变的时候，发送信息
			SmsManager manager = SmsManager.getDefault();
			
			manager.sendTextMessage("5556", null,
					"longitude:"+longitude+",latitude:"+latitude, 
					null, null);
			
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
