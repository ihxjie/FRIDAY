package com.xjie.myapplication;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GradeTableAdapter extends RecyclerView.Adapter<GradeTableAdapter.ViewHolder> {

    private List<GradeTable> mMarkList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView course,mark,xf,jd,ksxz;
        CardView cardView;
        View MarkView;

        public ViewHolder(View view){
            super(view);
            MarkView = view;
            cardView = (CardView)view.findViewById(R.id.grade_card_view);
            course = (TextView)view.findViewById(R.id.course);
            mark = (TextView)view.findViewById(R.id.mark);
            xf = (TextView)view.findViewById(R.id.xf);

            ksxz = (TextView)view.findViewById(R.id.courseProperties);

        }
    }

    public GradeTableAdapter(List<GradeTable> gradeTableList){
        mMarkList = gradeTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_grade,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.MarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                GradeTable gradeTable = mMarkList.get(position);
                Toast.makeText(v.getContext(),"绩点:"+ gradeTable.getCourseJd(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradeTable gradeTable = mMarkList.get(position);
        holder.course.setText(gradeTable.getCourseName());
        holder.mark.setText(gradeTable.getCourseMark());
        holder.xf.setText(gradeTable.getCourseXf());
        holder.ksxz.setText(gradeTable.getCourseProperties());
        //更改颜色(挂科
        if(Integer.parseInt(gradeTable.getCourseMark()) < 60){

            holder.cardView.setCardBackgroundColor(Color.rgb(198,40,40));
            holder.course.setTextColor(Color.rgb(255,255,255));
            holder.mark.setTextColor(Color.rgb(255,255,255));
            holder.xf.setTextColor(Color.rgb(255,255,255));
            holder.ksxz.setTextColor(Color.rgb(255,255,255));
        }

    }

    @Override
    public int getItemCount() {
        return mMarkList.size();
    }
}
