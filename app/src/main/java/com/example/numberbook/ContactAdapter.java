package com.example.numberbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> contactData;

    public ContactAdapter(List<Contact> contactData) {
        this.contactData = contactData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact currentContact = contactData.get(position);
        holder.nameLabel.setText(currentContact.getFullName());
        holder.phoneLabel.setText(currentContact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactData != null ? contactData.size() : 0;
    }

    public void refreshList(List<Contact> newList) {
        this.contactData = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameLabel, phoneLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameLabel = itemView.findViewById(android.R.id.text1);
            phoneLabel = itemView.findViewById(android.R.id.text2);
        }
    }
}
