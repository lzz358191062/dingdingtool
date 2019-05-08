package com.lzz.dingdingsigntool;

import org.greenrobot.eventbus.EventBus;

public class MessageEvent {

    public static void postAction(){
        EventBus.getDefault().post(new MessageEvent());
    }
}
