package com.poseidonapp.workorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poseidonapp.R;
import com.poseidonapp.workorder.activities.WorkInputActivity;
import com.poseidonapp.workorder.model.LaborData;


import java.util.List;

public class LaborAdapter extends RecyclerView.Adapter<LaborAdapter.ViewHolder>{

    private LayoutInflater mInflater;
//    WorkInputActivity context;
    Context context;
    List<LaborData> data;

    public LaborAdapter(Context context, List<LaborData> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data=data;
    }

//    public LaborAdapter(WorkInputActivity context, List<LaborData> data) {
//        this.context = context;
//        this.mInflater = LayoutInflater.from(context);
//        this.data=data;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.labor_table_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.hrsTV.setText(data.get(position).getHrs());
        holder.laborTV.setText(data.get(position).getLabor());
        holder.rateTV.setText(data.get(position).getRate());
        holder.amountTV.setText(String.valueOf(data.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(LaborData item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<LaborData> getData() {
        notifyDataSetChanged();
        return data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hrsTV;
        TextView laborTV;
        TextView rateTV;
        TextView amountTV;

        ViewHolder(View itemView) {
            super(itemView);
            hrsTV = itemView.findViewById(R.id.hrsTV);
            laborTV = itemView.findViewById(R.id.laborTV);
            rateTV = itemView.findViewById(R.id.rateTV);
            amountTV = itemView.findViewById(R.id.amountlaborTV);
        }
    }
}
