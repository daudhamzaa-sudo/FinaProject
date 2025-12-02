package com.example.finaproject.data.MyTaskTable;


import com.example.finaproject.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
public class MyTaskAdapter extends ArrayAdapter<MyTask>
{
    private final int itemlayout;

    public MyTaskAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemlayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vitem= convertView;
        if(vitem==null)
            vitem= LayoutInflater.from(getContext()).inflate(itemlayout,parent,false);
        ImageView taskImage = vitem.findViewById(R.id.task_image);
        TextView taskTitle = vitem.findViewById(R.id.task_title);
        TextView taskDescription = vitem.findViewById(R.id.task_description);
        TextView taskStatus = vitem.findViewById(R.id.task_status);

//קבלת הנתון (עצם) הנוכחי
        MyTask current=getItem(position);
        //הצגת הנתונים על שדות הרכיב הגרפי
        //taskTitle.setText(current.getShortTitle());
       // taskDescription.setText(current.getText());
      //  taskStatus.setText("Importance:"+current.getImportance());


        return vitem;

        //return super.getView(position, convertView, parent);
    }
}
