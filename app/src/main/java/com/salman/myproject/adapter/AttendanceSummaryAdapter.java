package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.salman.myproject.R;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.RealmStudent;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Salman on 1/2/2018.
 */

public class AttendanceSummaryAdapter extends RecyclerView.Adapter<AttendanceSummaryAdapter.Holder>  {

    private ArrayList<RealmStudent> mList;
    private LayoutInflater mInflater;
    private Realm mRealm;
    private boolean mEnabled = false;
    private checkBoxClick mClick;
    private String date;

    public interface checkBoxClick
    {
        void presentClick(int p);
        void absentClick(int p);
        void leaveClick(int p);
    }

    public AttendanceSummaryAdapter(Context context,ArrayList<RealmStudent> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mRealm = Realm.getDefaultInstance();
    }

    public void setClick(checkBoxClick click) {
        mClick = click;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.attendance_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        holder.absent.setEnabled(mEnabled);
        holder.leave.setEnabled(mEnabled);
        holder.present.setEnabled(mEnabled);

        RealmStudent student = mList.get(position);
        String s = student.getName()+"\n"+student.getRollNumber();
        holder.name.setText(s);

        Attendance attendance = mRealm.where(Attendance.class).equalTo("faceId",student.getFaceId())
                .equalTo("course",student.getCourseName()).equalTo("date",date).findFirst();

        if(attendance!=null)
        {
            if (attendance.isPresent()) {
                holder.present.setChecked(true);
                holder.leave.setChecked(false);
                holder.absent.setChecked(false);
            } else if (attendance.isAbsent()) {
                holder.present.setChecked(false);
                holder.leave.setChecked(false);
                holder.absent.setChecked(true);
            } else if (attendance.isLeave()) {
                holder.present.setChecked(false);
                holder.leave.setChecked(true);
                holder.absent.setChecked(false);
            }
            else
            {
                holder.present.setChecked(false);
                holder.leave.setChecked(false);
                holder.absent.setChecked(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    class Holder extends RecyclerView.ViewHolder
    {

        TextView name;
        CheckBox present,absent,leave;

        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView);
            present = itemView.findViewById(R.id.checkBox);
            absent = itemView.findViewById(R.id.checkBox1);
            leave = itemView.findViewById(R.id.checkBox2);

            present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.presentClick(getAdapterPosition());
                }
            });

            absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.absentClick(getAdapterPosition());

                }
            });

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.leaveClick(getAdapterPosition());

                }
            });

        }
    }

}
