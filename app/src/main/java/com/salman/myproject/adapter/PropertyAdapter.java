package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.myproject.R;
import com.salman.myproject.realm_pojo.PropertyDetail;

import java.util.ArrayList;

/**
 * Created by Salman on 1/3/2018.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.Holder> {


    private ArrayList<PropertyDetail> mList;
    private LayoutInflater mInflater;
    private Context mContext;
    private propertyClick pc;

    public interface propertyClick
    {
        void setPropertyClick(int p);
    }

    public PropertyAdapter(Context context, ArrayList<PropertyDetail> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setPc(propertyClick pc) {
        this.pc = pc;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.property_card_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        PropertyDetail detail = mList.get(position);

        holder.shortDesc.setText(detail.getDealType());
        holder.location.setText(detail.getLocation());
        String price = "Rs: "+String.valueOf(detail.getPrice());
        holder.price.setText(price);

        Glide.with(mContext)
                .load(detail.getPhotoLink())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        private View view;
        private ImageView imageView;
        private TextView shortDesc,price,location;

        public Holder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.property_view);
            imageView = itemView.findViewById(R.id.property_image);
            shortDesc = itemView.findViewById(R.id.property_disc);
            price = itemView.findViewById(R.id.price);
            location = itemView.findViewById(R.id.location);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pc.setPropertyClick(getAdapterPosition());
                }
            });


        }
    }
}
