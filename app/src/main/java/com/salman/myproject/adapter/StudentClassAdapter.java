package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salman.myproject.R;
import com.salman.myproject.pojo.TeacherInfo;

import java.util.ArrayList;

/**
 * Created by Salman on 1/7/2018.
 */

public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.Holder> {

    private ArrayList<TeacherInfo> mList;
    private LayoutInflater mInflater;
    private clickCardView mCardView;

    public interface clickCardView
    {
        void cardViewClicked(int p);
    }

    public void setCardView(clickCardView cardView) {
        mCardView = cardView;
    }

    public StudentClassAdapter(Context context, ArrayList<TeacherInfo> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.student_class_enroll_recycler_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        TeacherInfo info = mList.get(position);

        holder.className.setText(info.getClassName());
        holder.teacherName.setText(info.getTeacherName());

        if(info.isDeleted())
            holder.deleted.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        TextView teacherName,className,deleted;
        CardView cardView;
        public Holder(View itemView) {
            super(itemView);

            teacherName = itemView.findViewById(R.id.teacher_name_recycler);
            className  = itemView.findViewById(R.id.course_name_recycler);
            deleted = itemView.findViewById(R.id.student_text_deleted);
            cardView = itemView.findViewById(R.id.card_view);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCardView.cardViewClicked(getAdapterPosition());
                }
            });
        }
    }
}
