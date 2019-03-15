package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "Transactions")
@TypeConverters(DateConverter.class)
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "transaction_hash")
    public String transactionHash;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "receiver_address")
    public String receiverAddress;

    public String amount;

    @ColumnInfo(name = "gas_limit")
    public String gasLimit;

    @ColumnInfo(name = "gas_price")
    public String gasPrice;

    @ColumnInfo(name = "sender_address")
    public String senderAddress;
}



