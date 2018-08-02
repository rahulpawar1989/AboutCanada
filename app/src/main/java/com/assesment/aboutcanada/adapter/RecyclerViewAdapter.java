package com.assesment.aboutcanada.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assesment.aboutcanada.R;
import com.assesment.aboutcanada.activity.GlideApp;
import com.assesment.aboutcanada.model.CityInfoRow;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<CityInfoRow> mAndroidList;
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<CityInfoRow> androidList) {
        this.mContext = mContext;
        mAndroidList = androidList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTitle.setText(mAndroidList.get(position).getTitle());
        holder.mDesc.setText(mAndroidList.get(position).getDescription());
        GlideApp.with(mContext)
                .load(mAndroidList.get(position).getImageHref())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.mImg);
    }

    @Override
    public int getItemCount() {
        return mAndroidList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle, mDesc;
        private ImageView mImg;

        ViewHolder(View view) {
            super(view);

            mTitle = view.findViewById(R.id.tittle);
            mDesc = view.findViewById(R.id.description);
            mImg = view.findViewById(R.id.img);
        }
    }


}
