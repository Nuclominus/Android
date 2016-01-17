package com.nuclominus.offlinetwitterclient.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nuclominus.offlinetwitterclient.DataObj.TweetObj;
import com.nuclominus.offlinetwitterclient.R;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<TweetObj> items;
    private Context context;

    public TweetAdapter(List<TweetObj> tweets, Context context) {
        this.items = tweets;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TweetObj item = items.get(i);
        item.updateDiff();

        Glide.with(context)
                .load(item.getImg())
                .placeholder(R.mipmap.ic_tweet)
                .crossFade()
                .into(viewHolder.avatar);

        viewHolder.tVTitle.setText(item.getUser());
        viewHolder.tVTag.setText("@" + item.getScreen_name());
        viewHolder.tvTime.setText(item.getDiffTime());
        viewHolder.tVContent.setText(item.getText());
        viewHolder.tVContent.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView avatar;
        protected TextView tVTitle, tVTag, tvTime, tVContent;


        public ViewHolder(final View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.iVAvatar);
            tVTitle = (TextView) itemView.findViewById(R.id.tVTitle);
            tVTag = (TextView) itemView.findViewById(R.id.tVTag);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tVContent = (TextView) itemView.findViewById(R.id.tVContent);
        }

    }

}