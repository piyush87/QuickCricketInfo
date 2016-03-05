package com.panduka.quickcricketinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.panduka.quickcricketinfo.R;
import com.panduka.quickcricketinfo.activities.MatchActivity;
import com.panduka.quickcricketinfo.app.AppConfig;
import com.panduka.quickcricketinfo.app.AppController;
import com.panduka.quickcricketinfo.datastructure.CricketMatch;


import java.util.List;

/**
 * Created by pandukadesilva on 2/22/16.
 */
public class CricMatchAdapter extends RecyclerView.Adapter<CricMatchAdapter.ViewHolder> {
    ImageLoader mImageLoader;

    List<CricketMatch> mDataSet;
    Context mCxt;

    public CricMatchAdapter(List<CricketMatch> dataSet) {
        this.mDataSet = dataSet;
        mImageLoader = AppController.getInstance().getImageLoader();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cric_match_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        mCxt = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtNameTeam1.setText(mDataSet.get(position).team1.teamName);
        holder.txtNameTeam2.setText(mDataSet.get(position).team2.teamName);
        holder.txtScoreTeam1.setText(mDataSet.get(position).team1.score);
        holder.txtScoreTeam2.setText(mDataSet.get(position).team2.score);
        holder.txtScore1Team1.setText(mDataSet.get(position).team1.score1);
        holder.txtScore1Team2.setText(mDataSet.get(position).team2.score1);
        holder.txtDescription.setText(mDataSet.get(position).matchDescription);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(AppConfig.FACEBOOK_PAGE))
                .setContentDescription(mDataSet.get(position).matchDescription)
                .setContentTitle(mDataSet.get(position).team1.teamName + " VS " + mDataSet.get(position).team2.teamName)
                .build();
        holder.btnShare.setShareContent(content);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent i = new Intent(mCxt, MatchActivity.class);
                mCxt.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView txtNameTeam1;
        public TextView txtNameTeam2;
        public TextView txtScoreTeam1;
        public TextView txtScoreTeam2;
        public TextView txtScore1Team1;
        public TextView txtScore1Team2;
        public TextView txtDescription;
        public ShareButton btnShare;

        private ItemClickListener clickListener;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);

            txtNameTeam1 = (TextView) v.findViewById(R.id.txtNameTeam1);
            txtNameTeam2 = (TextView) v.findViewById(R.id.txtNameTeam2);
            txtScoreTeam1 = (TextView) v.findViewById(R.id.txtScoreTeam1);
            txtScoreTeam2 = (TextView) v.findViewById(R.id.txtScoreTeam2);
            txtScore1Team1 = (TextView) v.findViewById(R.id.txtScore1Team1);
            txtScore1Team2 = (TextView) v.findViewById(R.id.txtScore1Team2);
            txtDescription = (TextView) v.findViewById(R.id.txtDescription);
            btnShare = (ShareButton)v.findViewById(R.id.fbShare);


        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onClick(v, getPosition(), true);
            return true;
        }
    }
}
