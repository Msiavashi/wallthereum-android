package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface TransactionDAO {
//    @Insert
//    void insertSingleHash(String hash);

    @Query("SELECT * FROM Transactions")
    List<TransactionEntity> getTransactions();

    @Query("SELECT * FROM transactions WHERE sender_address = :walletAddress ORDER BY created_at DESC")
    List<TransactionEntity> getWalletTransactions(String walletAddress);

    @Query("SELECT * FROM Transactions WHERE sender_address = :address")
    LiveData<List<TransactionEntity>> getTransactionsLive(String address);

    @Query("SELECT * FROM Transactions WHERE id = :id")
    TransactionEntity getTransaction(int id);


    @Delete
    void deleteSingleTransaction(TransactionEntity transactionEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
