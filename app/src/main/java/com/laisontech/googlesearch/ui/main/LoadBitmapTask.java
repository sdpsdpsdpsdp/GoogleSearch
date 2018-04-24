package com.laisontech.googlesearch.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.laisontech.googlesearch.interfaces.OnLoadDataFromTaskListener;
import com.laisontech.googlesearch.utils.WindowUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SDP on 2018/4/24.
 */

public class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private OnLoadDataFromTaskListener taskListener;

    public LoadBitmapTask(OnLoadDataFromTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            //连接
            connection.connect();
            //得到响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (taskListener != null) {
            taskListener.onLoadDataFromTask(bitmap);
        }
    }
}
