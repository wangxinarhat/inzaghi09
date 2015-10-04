package com.milan.inzaghi09.receiver;

import com.milan.inzaghi09.utils.ConstantValues;
import com.milan.inzaghi09.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 1手机重启后，根据bootreceiver配置的清单文件中的action触发bootreceiver，进入onReceiver方法
		// 2获取已经存储的sim卡序列号
		String sim_num = SpUtil.getString(context, ConstantValues.SIM_NUM, "");
		
		// 3获取当前sim卡序列号
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String current_sim_num = manager.getSimSerialNumber() ;
		
		// 4比对sim卡序列号
		if (!sim_num.equals(current_sim_num)) {
			// 4.1当前sim卡序列号与存储的不一致，发送报警短信
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("5556", null, "simCard changed!", null,null);

		}

	}
}