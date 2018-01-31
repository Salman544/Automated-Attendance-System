package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.myproject.R;
import com.salman.myproject.firebase_pojo.AddStudent;

import java.util.ArrayList;

/**
 * Created by Salman on 1/7/2018.
 */

public class StudentRequestAdapter extends RecyclerView.Adapter<StudentRequestAdapter.Holder> {

    private ArrayList<AddStudent> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private clickAdapter mClickAdapter;

    public StudentRequestAdapter(ArrayList<AddStudent> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.recycler_view_student_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        AddStudent student = mList.get(position);
        holder.name.setText(student.getName());
        holder.id.setText(student.getRollNumber());

        Glide.with(mContext)
                .load(student.getPhotoLink())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.link);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface clickAdapter
    {
        void setRecClick(int p);
        void setRecLongClick(int p);
    }

    public void setClickAdapter(clickAdapter clickAdapter) {
        mClickAdapter = clickAdapter;
    }

    class Holder extends RecyclerView.ViewHolder
    {
        ImageView link;
        TextView name,id;
        View view;
        public Holder(View itemView) {
            super(itemView);

            link = itemView.findViewById(R.id.studentImage_rec);
            name = itemView.findViewById(R.id.student_name_rec);
            id = itemView.findViewById(R.id.student_id_rec);
            view = itemView.findViewById(R.id.student_view_layout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickAdapter.setRecClick(getAdapterPosition());
                }
            });

        }
    }
}
