package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salman.myproject.R;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.RealmStudent;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Salman on 1/1/2018.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.Holder> {

    private ArrayList<RealmStudent> mList;
    private LayoutInflater mInflater;
    private Context mContext;
    private clickAdapter mClickAdapter;
    private String date,course;
    private Realm mRealm;

    public StudentAdapter(ArrayList<RealmStudent> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRealm = Realm.getDefaultInstance();
    }

    public void setDate(String date)
    {
            this.date = date;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.recycler_view_student_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        RealmStudent student = mList.get(position);
        assert student != null;
        holder.name.setText(student.getName());
        holder.id.setText(student.getRollNumber());

        Glide.with(mContext)
                .load(student.getPhotoLink())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.link);

        Attendance attendance = mRealm.where(Attendance.class).equalTo("faceId",student.getFaceId())
                .equalTo("course",course).equalTo("date",date).findFirst();

        if(attendance!=null)
        {
            if (attendance.isPresent()) {
                holder.attendance.setText("P");
                holder.attendance.setVisibility(View.VISIBLE);
            } else if (attendance.isAbsent()) {
                holder.attendance.setText("A");
                holder.attendance.setVisibility(View.VISIBLE);
            } else if (attendance.isLeave()) {
                holder.attendance.setText("L");
                holder.attendance.setVisibility(View.VISIBLE);
            } else
                holder.attendance.setVisibility(View.GONE);
        }
        else
            holder.attendance.setVisibility(View.GONE);



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCourse(String course) {
        this.course = course;
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
        TextView name,id, attendance;
        View view;
        public Holder(View itemView) {
            super(itemView);

            link = itemView.findViewById(R.id.studentImage_rec);
            name = itemView.findViewById(R.id.student_name_rec);
            id = itemView.findViewById(R.id.student_id_rec);
            view = itemView.findViewById(R.id.student_view_layout);
            attendance = itemView.findViewById(R.id.attedence_text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickAdapter.setRecClick(getAdapterPosition());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mClickAdapter.setRecLongClick(getAdapterPosition());
                    return true;
                }
            });

        }
    }

}
