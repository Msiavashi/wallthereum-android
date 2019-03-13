package com.wallthereum.wallthereum.coin.Ethereum.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TransactionEntity.class}, version = 2, exportSchema = false)
public abstract class TransactionDB extends RoomDatabase {
    public abstract TransactionDAO transactionDAO();
    private static volatile TransactionDB INSTANCE;

    public static TransactionDB getTransactionDB(final Context context){
        if (INSTANCE == null){
            synchronized (TransactionDB.class) {
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TransactionDB.class, "transactions_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
