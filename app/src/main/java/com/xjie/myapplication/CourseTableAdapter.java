package com.xjie.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CourseTableAdapter extends RecyclerView.Adapter<CourseTableAdapter.ViewHolder> {

    private List<CourseTable> mCourseList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView course,place,teacher,time;
        View courseView;

        public ViewHolder(View view){
            super(view);
            courseView = view;
            course = (TextView)view.findViewById(R.id.course);
            place = (TextView)view.findViewById(R.id.place);
            time = (TextView)view.findViewById(R.id.time);
            teacher = (TextView)view.findViewById(R.id.teacher);
        }
    }

    public CourseTableAdapter(List<CourseTable> courseTableList){
        mCourseList = courseTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CourseTable course = mCourseList.get(position);
                Toast.makeText(v.getContext(),"DETAIL:"+course.getWeek()+","+course.getXqj(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseTable course = mCourseList.get(position);
        holder.course.setText(course.getCourse());
        holder.teacher.setText(course.getTeacher());
        holder.place.setText(course.getPlace());
        holder.time.setText(course.getTime());

    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }
}
