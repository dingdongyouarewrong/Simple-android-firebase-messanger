package com.example.messanger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messanger.RoomClasses.DialogModel;

import java.util.List;


public class DialogsListAdapter extends RecyclerView.Adapter<DialogsListAdapter.DialogsViewHolder> {

    private ItemClickListener mClickListener;
    private final LayoutInflater mInflater;
    private List<DialogModel> mDialogs; // Cached copy of words

    public DialogsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DialogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.dialog_element, parent, false);
        return new DialogsViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(DialogsViewHolder holder, int position) {
        DialogModel currentDialog = mDialogs.get(position);
        holder.dialogNameView.setText(currentDialog.dialog_name);
        holder.dialogIdView.setText(currentDialog.dialog_id);
        holder.coloredCircle.setBackgroundColor(currentDialog.dialog_color);

    }

    public void setData(List<DialogModel> dialogs){
        mDialogs = dialogs;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mDialogs != null)
            return mDialogs.size();
        else return 0;
    }

    public void setOnClick(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
        Log.e("onClick set", "works");

    }


    class DialogsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView dialogNameView;
        private final TextView dialogIdView;
        private final View coloredCircle;
        ItemClickListener itemClickListener;

        private DialogsViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;
            dialogNameView = itemView.findViewById(R.id.dialogNameElement);
            dialogIdView = itemView.findViewById(R.id.dialogIdElement);
            coloredCircle = itemView.findViewById(R.id.coloredCircle);

            itemView.setOnClickListener(this);
            Log.e("Holder", "works");
        }

        @Override
        public void onClick(View v) {
            Log.e("onClick in adapter ", "works");

            itemClickListener.onItemClick(v, getAdapterPosition());
        }

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}