package com.wallthereum.wallthereum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        public TextView creationDate;
        public TextView receiverAddress;
        public TextView txnFee;

        public MyViewHolder(View view) {
            super(view);
            hash = view.findViewById(R.id.item_hash);
            creationDate = view.findViewById(R.id.item_creation_date);
            receiverAddress = view.findViewById(R.id.item_receiver_address);
            txnFee = view.findViewById(R.id.item_fee);
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
        BigDecimal fee = SendTransactionActivity.transactionFee(new BigDecimal(transactionEntity.gasPrice), new BigInteger(transactionEntity.gasLimit));
        holder.hash.setText("Hash: " + transactionEntity.transactionHash);
        holder.receiverAddress.setText("receiver: " + transactionEntity.receiverAddress);
        holder.creationDate.setText(transactionEntity.createdAt.toString());
        holder.txnFee.setText("fee: " + fee.toString());
    }

    @Override
    public int getItemCount() {
        return this.transactionsList.size();
    }

}
