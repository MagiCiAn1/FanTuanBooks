package com.anonymouser.book.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.anonymouser.book.bean.PaintInfo;

/**
 * Created by YandZD on 2017/7/18.
 */

public class PowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 40);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

        PaintInfo.INSTANCE.setPowerNum((int) (level / (float) scale * 100F));
    }
}
