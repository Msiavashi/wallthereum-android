package com.wallthereum.wallthereum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {
    private List<TransactionEntity> transactionsList;

    public TransactionsAdapter(List<TransactionEntity> transactionsList){
        this.transactionsList = transactionsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView hash;
        public TextView creation_date;
        public TextView receiverAddress;

        public MyViewHolder(View view) {
            super(view);
            hash = view.findViewById(R.id.item_hash);
            creation_date = view.findViewById(R.id.item_creation_date);
            receiverAddress = view.findViewById(R.id.item_receiver_address);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionEntity transactionEntity = this.transactionsList.get(position);
        holder.hash.setText(transactionEntity.transactionHash);
        holder.receiverAddress.setText(transactionEntity.receiverAddress);
        holder.creation_date.setText(transactionEntity.createdAt.toString());
    }

    @Override
    public int getItemCount() {
        return this.transactionsList.size();
    }

}
