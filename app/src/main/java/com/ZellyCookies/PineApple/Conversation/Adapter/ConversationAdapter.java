package com.ZellyCookies.PineApple.Conversation.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ZellyCookies.PineApple.Conversation.Object.MessageObject;
import com.ZellyCookies.PineApple.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MessageViewHolder> {

    private static final String TAG = "MessageAdapter";

    ArrayList<MessageObject> messageList;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser mFirebaseUser;

    public ConversationAdapter(ArrayList<MessageObject> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layoutView;
        if (viewType == MSG_TYPE_RIGHT) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_message_right, null, false);
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_message_left, null, false);
        }

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public int getItemViewType(int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageList.get(position).getSenderId().equals(mFirebaseUser.getUid())) {
            Log.d(TAG, "getItemViewType: sender");
            return MSG_TYPE_RIGHT;
        } else {
            Log.d(TAG, "getItemViewType: recieved");
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageViewHolder holder, int position) {
        holder.mTimeSend.setText(messageList.get(position).getDatetime());
        holder.mMessage.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    //message holder subclass
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mMessage, mTimeSend;
        LinearLayout mLayout;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.d(TAG, "MessageViewHolder: is actived");
            mLayout = itemView.findViewById(R.id.sendLayout);

            mTimeSend = itemView.findViewById(R.id.time_send);
            mMessage = itemView.findViewById(R.id.message);
        }
    }
}
