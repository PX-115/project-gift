package com.example.project_gift.adapter.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Equipamento;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EquipamentoAdapter extends FirestoreRecyclerAdapter<Equipamento, EquipamentoAdapter.EquipamentoHolder> {
    private OnItemClickListener listener;

    public EquipamentoAdapter(@NonNull FirestoreRecyclerOptions<Equipamento> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EquipamentoHolder holder, int position, @NonNull Equipamento model) {
        holder.textViewSSID.setText(model.getDisplayName());
        holder.textViewMac.setText(model.getMacAdress());
    }

    @NonNull
    @Override
    public EquipamentoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.curso_item, parent, false);
        return new EquipamentoHolder(v);
    }

    class EquipamentoHolder extends RecyclerView.ViewHolder {

        private TextView textViewSSID;
        private TextView textViewMac;

        public EquipamentoHolder(@NonNull View itemView) {
            super(itemView);

            textViewSSID = itemView.findViewById(R.id.textViewSSID);
            textViewMac = itemView.findViewById(R.id.textViewMac);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
