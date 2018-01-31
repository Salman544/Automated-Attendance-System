package com.salman.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salman.myproject.R;

import java.util.ArrayList;

/**
 * Created by Salman on 12/31/2017.
 */

public class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.Holder> {

    private ArrayList<String> mList;
    private LayoutInflater mInflater;
    private Click mClick;

    public interface Click
    {
        void setOnTextClick(int p);
    }

    public void setClick(Click click) {
        mClick = click;
    }

    public TeacherClassAdapter(Context context, ArrayList<String> list)
    {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.teacher_class_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        holder.textView.setText(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        TextView textView;
        View view;
        public Holder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.teacherClassName);
            view = itemView.findViewById(R.id.teacherClassLayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.setOnTextClick(getAdapterPosition());
                }
            });


        }
    }

}
