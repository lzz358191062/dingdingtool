package com.lzz.dingdingsigntool;

import android.app.Application;
import android.content.Context;


/**
 * @author lzz
 * @time 19-5-7 下午5:12
 */
public class DingdingAplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
