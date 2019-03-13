package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface TransactionDAO {
//    @Insert
//    void insertSingleHash(String hash);

    @Query("SELECT * FROM Transactions")
    List<TransactionEntity> getTransactions();

    @Query("SELECT * FROM Transactions")
    LiveData<List<TransactionEntity>> getTransactionsLive();

    @Query("SELECT * FROM Transactions WHERE tid = :tid")
    TransactionEntity getTransaction(int tid);

    @Insert
    void insertSingleTransaction(TransactionEntity transactionEntity);

//    @Insert
//    void insertGasPrice(BigInteger gasPrice);
//
//    @Insert
//    void insertGasLimit(BigInteger gasLimit);
//
//    @Insert
//    void insertCreatedAt(Date date);
//
//    @Insert
//    void insertAmount(String amount);
//
//    @Insert
//    void insertReceiverAddress(String address);
}
