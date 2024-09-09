package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private OnItemClickListener listener;
    private boolean deleteMode = false;

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
        void onDeleteClick(int position);
        void onFavoriteClick(int position);
    }

    public ContactAdapter(List<Contact> contactList, OnItemClickListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact, listener, deleteMode);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ImageButton btnDelete, btnFavorite;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }

        public void bind(Contact contact, OnItemClickListener listener, boolean deleteMode) {
            textViewName.setText(contact.getName());

            if (deleteMode) {
                btnDelete.setVisibility(View.VISIBLE);
                btnFavorite.setVisibility(View.GONE);
            } else {
                btnDelete.setVisibility(View.GONE);
                btnFavorite.setVisibility(View.VISIBLE);
                btnFavorite.setImageResource(contact.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });

            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoriteClick(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(contact);
                }
            });
        }
    }
}
