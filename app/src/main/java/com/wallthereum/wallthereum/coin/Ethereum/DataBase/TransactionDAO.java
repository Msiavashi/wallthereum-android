package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TransactionDAO {
    @Insert
    void insertSingleHash(String hash);

    @Query("SELECT * FROM Transactions")
    List<TransactionEntity> getTransactions();

    @Query("SELECT * FROM Transactions")
    LiveData<List<TransactionEntity>> getTransactionsLive();
}
