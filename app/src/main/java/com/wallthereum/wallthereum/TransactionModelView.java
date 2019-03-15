package com.wallthereum.wallthereum;

import android.app.Application;

import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDAO;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDB;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TransactionModelView extends AndroidViewModel {

    private TransactionDAO transactionDAO;
    private ExecutorService executorService;

    public TransactionModelView(@NonNull Application application) {
        super(application);
        transactionDAO = TransactionDB.getTransactionDB(application).transactionDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    LiveData<List<TransactionEntity>> getAllTransactions() {
        return transactionDAO.getTransactionsLive(Wallet.getWallet().getAddress());
    }

    void saveTransaction(TransactionEntity transactionEntity) {
        executorService.execute(() -> transactionDAO.insertSingleTransaction(transactionEntity));
    }

    void deleteTransaction(TransactionEntity transactionEntity) {
        executorService.execute(() -> transactionDAO.deleteSingleTransaction(transactionEntity));
    }
}