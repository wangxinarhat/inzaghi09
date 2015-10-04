package com.milan.inzaghi09.receiver;

import com.milan.inzaghi09.R;
import com.milan.inzaghi09.service.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 1当手机收到短信时，触发此方法
		// 2监听短信内容
		Object[] objects = (Object[]) intent.getExtras().get("pdus");

		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String body = message.getMessageBody();
			if (TextUtils.isEmpty(body)) {
				return;
			}

			DevicePolicyManager DPM = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			ComponentName componentName = new ComponentName(context,
					DeviceAdmin.class);

			// 3判断是否含有特殊字段
			if (body.contains("#*location*#")) {
				// 4定位
				// bindService(service, conn, flags)
				context.startService(new Intent(context, LocationService.class));

			} else if (body.contains("#*alarm*#")) {
				// 5播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.morning);
				player.setLooping(true);
				player.setVolume(1.0f, 1.0f);
				player.start();
			} else if (body.contains("#*wipedata*#")) {
				// 6删除数据
				if (DPM.isAdminActive(componentName)) {
					DPM.wipeData(0);
				}

			} else if (body.contains("#*lockscreen*#")) {
				// 7锁屏
				if (DPM.isAdminActive(componentName)) {
					DPM.lockNow();
					DPM.resetPassword("123", 0);
				}

			}
		}

	}

}
