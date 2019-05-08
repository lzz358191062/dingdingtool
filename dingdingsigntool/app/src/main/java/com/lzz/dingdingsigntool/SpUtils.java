package com.lzz.dingdingsigntool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * @author lzz
 * @time 19-5-6 下午5:33
 */
public class SpUtils {
    public static void savaFileToSP(String filecontent) {
        SharedPreferences mSharedPreferences = DingdingAplication.mContext.getSharedPreferences("daka_sp", Context.MODE_PRIVATE);
        String old = mSharedPreferences.getString("log", "");
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putString("log", old + filecontent);
        e.commit();
        MessageEvent.postAction();
    }

    public static String readFromSP() {
        SharedPreferences mSharedPreferences = DingdingAplication.mContext.getSharedPreferences("daka_sp", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("log", "no data");
    }

    public static void clearFromSP() {
        SharedPreferences mSharedPreferences = DingdingAplication.mContext.getSharedPreferences("daka_sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putString("log", "");
        e.commit();
    }


    /*public static boolean InitFile() {
        *//*文件的初始化：（创建需要的文件）*//*
        // 文件是否创建成功
        File logFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "daka.txt");
        boolean result = false;
        // Make sure log file is exists
        LogUtil.d("lzz-date", "file path = " + logFile.getPath());
        if (!logFile.exists()) {
            try {
                result = logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            result = true;
        }
        return result;
    }

    public static String readFromSD() throws IOException {
        File logFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "daka.txt");
        if (!logFile.exists()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        //打开文件输入流
        FileInputStream input = new FileInputStream(logFile);
        byte[] temp = new byte[1024];

        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        String rv = sb.toString();
        if(TextUtils.isEmpty(rv)){
            rv = readFromSP();
        }
        return rv;
    }

    public static void savaFileToSD(String filecontent) throws Exception {
        savaFileToSP(filecontent);
        File logFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "daka.txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        FileOutputStream output = null;
        try {
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            output = new FileOutputStream(logFile,true);
            output.write(filecontent.getBytes());
        }catch (Exception e){

        }finally {
            if(output!=null){
                output.close();
            }
            //关闭输出流
            MessageEvent.postAction();
        }

        //将String字符串以字节流的形式写入到输出流中

    }

    public static void DeleteFileFromSD()  {
        //这里就不要用openFileOutput了,那个是往手机内存中写数据的
        File logFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "daka.txt");
        logFile.delete();
        clearFromSD();
    }*/



}
