package com.libin.bd_voice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EventManager mWpEventManager;
    private static final String TAG = "ActivityWakeUp";
    private TextView txtLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLog = (TextView) findViewById(R.id.txtLog);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(MainActivity.this, "wp");
        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
                        txtLog.append("唤醒成功, 唤醒词: " + word + "\r\n");
                    } else if ("wp.exit".equals(name)) {
                        txtLog.append("唤醒已经停止: " + params + "\r\n");
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });
        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 停止唤醒监听
        mWpEventManager.send("wp.stop", null, null, 0, 0);
    }
}
