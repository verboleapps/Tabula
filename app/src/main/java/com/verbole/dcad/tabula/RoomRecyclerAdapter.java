package com.verbole.dcad.tabula;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verbole.dcad.tabula.RoomDatabase.Baseentrees;

import java.util.List;

public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.ViewHolder> {
    Context mContext;
    List<Baseentrees> mBaseEntrees;

    RoomRecyclerAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_row_texte,parent,false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RoomRecyclerAdapter.ViewHolder holder, int position) {
        if (mBaseEntrees != null) {
            Baseentrees current = mBaseEntrees.get(position);
            Log.d("RECYCLE","bind ?? " + position);
            holder.item.setText(current.getEntree());
        }
        else {
            holder.item.setText("yo");
        }
    }

    void setEntrees(List<Baseentrees> entrees) {
        mBaseEntrees = entrees;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mBaseEntrees != null) {
            return mBaseEntrees.size();
        }
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.listRowVue);
        }
    }
}
