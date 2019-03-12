package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TransactionEntity.class}, version = 1, exportSchema = false)
public abstract class TransactionDB extends RoomDatabase {
    public abstract TransactionDAO transactionDAO();

}
