package com.laisontech.googlesearch.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.laisontech.googlesearch.interfaces.OnEditTextSearchListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by SDP on 2018/4/23.
 */

public class WindowUtils {
    /**
     * 设置布局文件可以在状态栏内显示
     */
    public static void setLayoutInStatusBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 隐藏状态栏
     */
    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    /**
     * 显示状态栏
     *
     * @param statusBarColor 状态栏的文字的颜色
     */
    public static void showStatusBar(Activity activity, int statusBarColor) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
        //设置状态栏的颜色，根据android 设置的版本进行显示
        StatusBarCompat.setStatusBarColor(activity, statusBarColor, true);
    }

    public static void setEtEvent(EditText etSearch, final ImageView ivDelete, final ProgressBar pbSearch, final OnEditTextSearchListener listener) {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.toString().length();
                if (length > 0) {
                    ivDelete.setVisibility(View.VISIBLE);
                    if (listener != null) {
                        listener.onSearch();
                    }
                } else {
                    ivDelete.setVisibility(View.GONE);
                    pbSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                if (length == 0) {
                    ivDelete.setVisibility(View.GONE);
                    pbSearch.setVisibility(View.GONE);
                    if (listener!=null){
                        listener.onShowLocalData();
                    }
                }
            }
        });
    }

    public static Bitmap GetLocalOrNetBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try
        {
            in = new BufferedInputStream(new URL(url).openStream(), 200);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 200);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
