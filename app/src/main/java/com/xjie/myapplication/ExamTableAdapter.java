package com.xjie.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExamTableAdapter extends RecyclerView.Adapter<ExamTableAdapter.ViewHolder> {

    private List<ExamTable> mExamList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView course,place,time,seat;
        View ExamView;

        public ViewHolder(View view){
            super(view);
            ExamView = view;
            course = (TextView)view.findViewById(R.id.course);
            place = (TextView)view.findViewById(R.id.place);
            time = (TextView)view.findViewById(R.id.time);
            seat = (TextView)view.findViewById(R.id.seat);
        }
    }

    public ExamTableAdapter(List<ExamTable> ExamTableList){
        mExamList = ExamTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_exam,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ExamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ExamTable examTable = mExamList.get(position);
                Toast.makeText(v.getContext(),"DETAIL:",Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamTable examTable = mExamList.get(position);
        holder.course.setText(examTable.getCourseName());
        holder.time.setText(examTable.getExamTime());
        holder.place.setText(examTable.getExamLocation());
        holder.seat.setText(examTable.getExamSeat());

    }

    @Override
    public int getItemCount() {
        return mExamList.size();
    }
}
