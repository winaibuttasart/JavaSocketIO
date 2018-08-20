package com.example.nai.javasocketio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {
    private ArrayList<String> imageurl;
    private ArrayList<String> messageId;
    private ArrayList<String> msg;
    private ArrayList<String> time;
    private Context context;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    MessageListAdapter(Context context, ArrayList<String> imageurl, ArrayList<String> messageId, ArrayList<String> msg, ArrayList<String> time) {
        this.context = context;
        this.imageurl = imageurl;
        this.messageId = messageId;
        this.msg = msg;
        this.time = time;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageId.get(position).equals("me")) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(this.msg.get(position), this.time.get(position));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(this.imageurl.get(position), this.messageId.get(position), this.msg.get(position), this.time.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.imageurl.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(String msg, String time) {
            messageText.setText(msg);
            timeText.setText(time);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        ImageView image_message_profile;
        TextView text_message_name, text_message_body, text_message_time;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            image_message_profile = itemView.findViewById(R.id.image_message_profile);
            text_message_name = itemView.findViewById(R.id.text_message_name);
            text_message_body = itemView.findViewById(R.id.text_message_body);
            text_message_time = itemView.findViewById(R.id.text_message_time);
        }

        void bind(String imageurl, String messageId, String msg, String time) {
            Glide.with(context).load(imageurl).apply(RequestOptions.circleCropTransform()).into(image_message_profile);
            text_message_name.setText(messageId);
            text_message_body.setText(msg);
            text_message_time.setText(time);
        }
    }
}
