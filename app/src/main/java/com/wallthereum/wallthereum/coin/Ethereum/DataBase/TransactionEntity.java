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
    public int uid;

    @ColumnInfo(name = "transaction_hash")
    public String transactionHash;

    @ColumnInfo(name = "created_at")
    public Date createdAt;
}



