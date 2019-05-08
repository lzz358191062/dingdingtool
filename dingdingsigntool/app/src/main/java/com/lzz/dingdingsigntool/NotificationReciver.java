package com.lzz.dingdingsigntool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * Created by lzz on 2017/12/21.
 */

public class NotificationReciver extends BroadcastReceiver {
    Handler mhandler = new Handler();
    PowerManager pm;
    PowerManager.WakeLock mWakelock;
    AlarmManager alarmManager;
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtil.d("lzz-receiver","receive action today is :"+Calendar.getInstance().get(Calendar.DATE));
        ArrayList mqlist = intent.getIntegerArrayListExtra("setAlarm");
        if(mqlist!=null){
            for (int i = 0; i < mqlist.size(); i++) {
                LogUtil.d("lzz-select", "mlist  ===" + mqlist.get(i));
            }
        }
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //if(hour==8||hour==9){
            int time = getRandom();
            try {
                SpUtils.savaFileToSP("\n接受打开钉钉指令\n当前时间 " +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                SpUtils.savaFileToSP("\n" +time+"分钟后打开钉钉");
            } catch (Exception e) {
                e.printStackTrace();
            }
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "myapp:SimpleTimer");
            Calendar c = Calendar.getInstance();
            int WeekOfYear = c.get(Calendar.DAY_OF_WEEK);
            LogUtil.d("lzz-new", "WeekOfYear  == " + WeekOfYear);
            if (WeekOfYear != 1 && WeekOfYear != 7) {
                doCard(context,time);
            } else {
                if(mqlist!=null){
                    ArrayList mlist = intent.getIntegerArrayListExtra("setAlarm");
                    for (int i = 0; i < mlist.size(); i++) {
                        LogUtil.d("lzz-date", "mlist  ===" + mlist.get(i));
                    }
                    int day = c.get(Calendar.DATE);
                    if (mlist.contains(day)) {
                        doCard(context,time);
                    }
                }
            }
        //}
    }

    private void doCard(final Context context, int time) {
        Intent intent = new Intent(context, StartReciver.class);
        intent.setAction("com.lzz.notification_alrm_start");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP
                , System.currentTimeMillis()+1000*60*time, sender);
        LogUtil.d("lzz-new", "after start at time  " + time +" min ago");
    }

    private int getRandom() {
        Random random = new Random();
        int num = random.nextInt(10);
        LogUtil.d("lzz-date", "random num = " + num);
        return num;
    }


    private void doStartApplicationWithPackageName(Context context, String packagename) {
        String content = "\n启动时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        try {
            SpUtils.savaFileToSP(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWakelock.acquire();
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        mWakelock.release();
    }


}
