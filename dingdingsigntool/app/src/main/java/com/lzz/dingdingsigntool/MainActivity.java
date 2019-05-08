package com.lzz.dingdingsigntool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class MainActivity extends AppCompatActivity implements WeekAdapter.ItemCheckBoxSelect {


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.btn_test)
    Button btnTest;

    @BindView(R.id.log)
    TextView log;

    @BindView(R.id.btn_clear)
    Button btnClear;

    @BindView(R.id.week_rv)
    RecyclerView weekRv;

    private ArrayList<Integer> mSelectlist = new ArrayList<>();

    AlarmManager alarmManager;

    PowerManager pm;

    PowerManager.WakeLock mWakelock;

    private boolean start = false;

    static final String[] PERMISSIONS = new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<WeekDayInfo> weekDayInfoList = new ArrayList<>();

    private WeekAdapter mWeekAdapter;

    private  PendingIntent sender;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "my:SimpleTimer");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Date d = new Date();
        Date date = getMonthStart(d);
        Date monthEnd = getMonthEnd(d);
        while (!date.after(monthEnd)) {
            getSATURDAYWeek(date);
            System.out.println(sdf.format(date));
            date = getNext(date);
            LogUtil.d("lzz-date", "data = " + sdf.format(date));
        }

        weekRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mWeekAdapter = new WeekAdapter(this,weekDayInfoList,this);
        weekRv.setAdapter(mWeekAdapter);



        /*if (!new PermissionsChecker(this).lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            SpUtils.InitFile();
        }*/
        log.setText(SpUtils.readFromSP());
    }

    private static final int REQUEST_CODE = 0;

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }




    private void setAlarm() {
        if(start){
            alarmManager.cancel(sender);
        }
        for (int i = 0; i < mSelectlist.size(); i++) {
            LogUtil.d("lzz-new", "包含的周六  == " + mSelectlist.get(i));
        }
        Intent intent = new Intent(this, NotificationReciver.class);
        intent.putIntegerArrayListExtra("setAlarm", mSelectlist);
        intent.setAction("com.lzz.notification_alrm_dingding");
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        LogUtil.d("lzz-date", "setAlarm time = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTimeInMillis()));
        try {
            SpUtils.savaFileToSP("\n开启极速打卡日期 " + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTimeInMillis())+"，次日起开始自动打卡");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*alarmManager.setRepeating(AlarmManager.RTC_WAKEUP
            , calendar.getTimeInMillis(),
            1000 * 60 * 60 * 24, sender);*/


        alarmManager.set(AlarmManager.RTC_WAKEUP
            , System.currentTimeMillis() + 1000, sender);
        LogUtil.d("lzz-start", "start repeat");
        start = true;

    }

    private static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (1 - index));
        return calendar.getTime();
    }

    private static Date getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (-index));
        return calendar.getTime();
    }

    private static Date getNext(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public boolean getSATURDAYWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int index = calendar.get(Calendar.DAY_OF_MONTH);
            WeekDayInfo info = new WeekDayInfo("星期六", index);
            weekDayInfoList.add(info);
            return true;
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int index = calendar.get(Calendar.DAY_OF_MONTH);
            WeekDayInfo info = new WeekDayInfo("星期日", index);
            weekDayInfoList.add(info);
            return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.btn_start, /*R.id.btn_close, */R.id.btn_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                //设置闹钟
                if (start) {
                    closeAlarm();
                    btnStart.setText("开启");
                    btnStart.setBackground(null);
                } else {
                    setAlarm();
                    btnStart.setText("关闭");
                    btnStart.setBackgroundColor(getColor(R.color.colorAccent));
                }
                break;
            /*case R.id.btn_close:
                //设置闹钟
                closeAlarm();
                break;*/
            case R.id.btn_test:
                testAlarm();
                break;
            default:
                break;
        }
    }

    private void testAlarm() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doStartApplicationWithPackageName("com.alibaba.android.rimet");
            }
        }, 5000);
        /*Intent intent = new Intent();
        intent.setAction("com.lzz.notification_alrm_dingding");
        intent.putIntegerArrayListExtra("setAlarm",mSelectlist);
        sendBroadcast(intent);*/

    }

    private void closeAlarm() {
        Intent intent = new Intent(this, NotificationReciver.class);
        intent.setAction("com.lzz.notification_alrm_dingding");
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(sender);
        start = false;
        try {
            SpUtils.savaFileToSP("\n关闭闹钟");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mWakelock.isHeld()) {
            mWakelock.release();
        }
    }

    private void doStartApplicationWithPackageName(String packagename) {
        mWakelock.setReferenceCounted(false);
        mWakelock.acquire();
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
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
        List<ResolveInfo> resolveinfoList = getPackageManager()
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
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        log.setText(SpUtils.readFromSP());
    }

    @OnClick(R.id.btn_clear)
    public void onViewClicked() {
        SpUtils.clearFromSP();
        log.setText(SpUtils.readFromSP());
    }

    @Override
    public void click(int position, boolean isChecked) {
        LogUtil.d("lzz-select","--"+position+" select = "+isChecked);
        if(isChecked){
            mSelectlist.add(position);
        }else {
            mSelectlist.remove(Integer.valueOf(position));
        }
    }
}
