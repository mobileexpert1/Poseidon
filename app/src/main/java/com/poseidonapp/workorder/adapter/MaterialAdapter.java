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
import com.poseidonapp.workorder.model.MaterialData;

import java.util.List;


public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder>  {

    private LayoutInflater mInflater;
    Context context;
//    WorkInputActivity context;
    List<MaterialData> data;

    public MaterialAdapter(Context context, List<MaterialData> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data=data;
    }/*   public MaterialAdapter(WorkInputActivity context, List<MaterialData> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data=data;
    }*/


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.material_table_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.qtyTV.setText(data.get(position).getQty());
        holder.materialTV.setText(data.get(position).getMaterial());
        holder.msizeTV.setText(data.get(position).getSize());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(MaterialData item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<MaterialData> getData() {
        return data;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView qtyTV;
        TextView materialTV;
        TextView msizeTV;

        ViewHolder(View itemView) {
            super(itemView);
            qtyTV = itemView.findViewById(R.id.qtyTV);
            materialTV = itemView.findViewById(R.id.materialTV);
            msizeTV = itemView.findViewById(R.id.msizeTV);
        }
    }
}
