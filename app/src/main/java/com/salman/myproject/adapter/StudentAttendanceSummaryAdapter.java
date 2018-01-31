package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.salman.myproject.R;
import com.salman.myproject.firebase_pojo.FirebaseAttendance;

import java.util.ArrayList;

/**
 * Created by Salman on 1/7/2018.
 */

public class StudentAttendanceSummaryAdapter extends RecyclerView.Adapter<StudentAttendanceSummaryAdapter.Holder> {

    private ArrayList<FirebaseAttendance> mList;
    private LayoutInflater mInflater;

    public StudentAttendanceSummaryAdapter(Context context, ArrayList<FirebaseAttendance> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.attendance_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        FirebaseAttendance attendance = mList.get(position);
        holder.date.setText(attendance.getDate());

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

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {

        TextView date;
        CheckBox present,absent,leave;

        public Holder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.textView);
            present = itemView.findViewById(R.id.checkBox);
            absent = itemView.findViewById(R.id.checkBox1);
            leave = itemView.findViewById(R.id.checkBox2);

        }
    }
}
