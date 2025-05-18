package com.example.repairshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;


import java.util.ArrayList;


public class RepairItemAdapter
        extends RecyclerView.Adapter<RepairItemAdapter.ViewHolder>
        implements Filterable {

    private ArrayList<RepairItem> mShoppingData;
    private ArrayList<RepairItem> mSoppingDataAll;
    private Context mContext;
    private int lastPosition = -1;

    RepairItemAdapter(Context context, ArrayList<RepairItem> itemsData) {
        this.mShoppingData = itemsData;
        this.mSoppingDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public RepairItemAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(RepairItemAdapter.ViewHolder holder, int position) {

        RepairItem currentItem = mShoppingData.get(position);


        holder.bindTo(currentItem);


        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingData.size();
    }



    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<RepairItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mSoppingDataAll.size();
                results.values = mSoppingDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(RepairItem item : mSoppingDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.item_name);
            mInfoText = itemView.findViewById(R.id.item_info);
            mItemImage = itemView.findViewById(R.id.item_image);
            mPriceText = itemView.findViewById(R.id.item_price);

        }

        void bindTo(RepairItem currentItem){
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }
    }
}