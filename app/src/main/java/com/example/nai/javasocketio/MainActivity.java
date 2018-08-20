package com.example.nai.javasocketio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baoyz.widget.PullRefreshLayout;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private com.github.nkzawa.socketio.client.Socket mSocket;
    private RecyclerView reclerview_message_list;
    private EditText edittext_chatbox;

    private ArrayList<String> imageurl;
    private ArrayList<String> messageId;
    private ArrayList<String> msg;
    private ArrayList<String> time;

    private ChangeDataFormat dateFormat;
    private MessageListAdapter mAdapter;


    {
        try {
            mSocket = IO.socket("http://192.168.23.65:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String tmpMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).cancelAll();

        imageurl = new ArrayList<>();
        messageId = new ArrayList<>();
        msg = new ArrayList<>();
        time = new ArrayList<>();

        final PullRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        reclerview_message_list = findViewById(R.id.reclerview_message_list);
        edittext_chatbox = findViewById(R.id.edittext_chatbox);
        Button button_chatbox_send = findViewById(R.id.button_chatbox_send);
        button_chatbox_send.setOnClickListener(this);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        mSocket.connect();
        mSocket.on("chat message", onNewMessage);

        initRecyclerViewHomeYoungMap();
    }

    private void initRecyclerViewHomeYoungMap() {
        mAdapter = new MessageListAdapter(this, imageurl, messageId, msg, time);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        reclerview_message_list.setLayoutManager(mLayoutManager);
        reclerview_message_list.setItemAnimator(new DefaultItemAnimator());
        reclerview_message_list.setAdapter(mAdapter);
        reclerview_message_list.scrollToPosition(msg.size() - 1);
        mAdapter.notifyDataSetChanged();
    }

    private void reloadAdapter() {
        mAdapter.notifyDataSetChanged();
        reclerview_message_list.scrollToPosition(msg.size() - 1);
    }

    private void sendMessage() {
        String message = edittext_chatbox.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        addMessage("me", message);
        tmpMessage = message;
        edittext_chatbox.setText("");
        mSocket.emit("chat message", message);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String messageid;
                    String message;
                    try {
                        messageid = data.getString("messageId");
                        message = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    addMessage(messageid, message);
                }
            });
        }
    };

    private void addMessage(String messageid, String message) {
        if (!tmpMessage.equals(message)) {
            imageurl.add("https://lh3.googleusercontent.com/l6JAkhvfxbP61_FWN92j4ulDMXJNH3HT1DR6xrE7MtwW-2AxpZl_WLnBzTpWhCuYkbHihgBQ=w640-h400-e365");
            messageId.add(messageid);
            msg.add(message);
            dateFormat = new ChangeDataFormat();
            String showDateString = dateFormat.getHour() + ":" + dateFormat.getMinute() + ":" + dateFormat.getSecond() + " " + dateFormat.getDay() + "/" + dateFormat.getMonth() + "/" + dateFormat.getYear();
            time.add(showDateString);
            makeNotification(message + " " + showDateString);
            reloadAdapter();
        }
    }


    private void makeNotification(String alertDetail) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder = builder
                .setSmallIcon(R.mipmap.ic_send)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_send))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle("มีข้อความใหม่")
                .setTicker("setTicker")
                .setContentText(alertDetail)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);
        Objects.requireNonNull(notificationManager).notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_chatbox_send:
                sendMessage();
                break;
        }
    }
}
