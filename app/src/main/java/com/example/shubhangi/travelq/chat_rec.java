package com.example.shubhangi.travelq;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by shubhangi on 16-04-2018.
 */

public class chat_rec extends RecyclerView.ViewHolder {
    TextView query,response,q1,r1;
    public chat_rec(View itemView) {
        super(itemView);
        query=(TextView)itemView.findViewById(R.id.query);
        response=(TextView)itemView.findViewById(R.id.response);
        q1=itemView.findViewById(R.id.quer);
        r1=itemView.findViewById(R.id.resp);
    }
}
