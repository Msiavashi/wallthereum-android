package com.wallthereum.wallthereum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDB;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.content.ContextCompat.startActivity;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {
    private List<TransactionEntity> transactionsList;

    public TransactionsAdapter(List<TransactionEntity> transactionsList){
        this.transactionsList = transactionsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView hash;
        public TextView creationDate;
        public TextView receiverAddress;
        public TextView txnFee;
        public View v;
        public MyViewHolder(View view) {
            super(view);
            v = view;
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

        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(v.getContext())
                        .setItems(WalletActivity.longPressOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0: // option[0]    see status
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://etherscan.io/tx/" + transactionEntity.transactionHash));
                                        startActivity(WalletActivity.getContext(), browserIntent, null);
                                        break;
                                    case 1: // option[1]    delete
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                TransactionDB.getTransactionDB(WalletActivity.getContext()).transactionDAO().deleteSingleTransaction(transactionEntity);
                                            }
                                        });
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create();
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.transactionsList.size();
    }

}
