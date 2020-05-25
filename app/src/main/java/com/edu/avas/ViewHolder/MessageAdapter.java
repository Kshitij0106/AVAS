package com.edu.avas.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.avas.Model.Message;
import com.edu.avas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context context;
    private List<Message> messageList;
    private FirebaseAuth auth;
    private onImageClickedListener adapterListener;

    private static final int MSG_TYPE_SEND = 0;
    private static final int MSG_TYPE_RECD = 1;

    public interface onImageClickedListener {
        void onImageClicked(int pos);
    }

    public void setOnImageClicked(onImageClickedListener onImageClickedListener) {
        adapterListener = onImageClickedListener;
    }

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_msg_layout, parent, false);
            MessageViewHolder mvh = new MessageViewHolder(view);
            return mvh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_msg_layout, parent, false);
            MessageViewHolder mvh = new MessageViewHolder(view);
            return mvh;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        if (messageList.get(position).getType().equals("image")) {
            holder.showImage.setVisibility(View.VISIBLE);
            Picasso.get().load(messageList.get(position).getMessage()).fit().centerCrop().into(holder.showImage);
        } else if (messageList.get(position).getType().equals("text")) {
            holder.showMessage.setVisibility(View.VISIBLE);
            holder.showMessage.setText(messageList.get(position).getMessage());
        } else if (messageList.get(position).getType().equals("docs")) {
            holder.showDocs.setVisibility(View.VISIBLE);
            holder.showDocs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageList.get(position).getMessage()));
                    holder.showDocs.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView showMessage, showDocs;
        public ImageView showImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            showImage = itemView.findViewById(R.id.showImage);
            showDocs = itemView.findViewById(R.id.showDocs);

            showImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapterListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            adapterListener.onImageClicked(pos);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (messageList.get(position).getSenderId().equals(user.getUid())) {
            return MSG_TYPE_SEND;
        } else {
            return MSG_TYPE_RECD;
        }
    }
}