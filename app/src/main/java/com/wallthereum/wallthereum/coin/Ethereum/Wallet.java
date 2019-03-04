//TODO: add password validation

package com.wallthereum.wallthereum.coin.Ethereum;

import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.MainActivity;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

public class Wallet {
    private Credentials credentials;
    private Wallet(){}

    public BigInteger getBalance() throws ExecutionException, InterruptedException {
        Web3j connection = Network.getNetwork().getmConnection();
        EthGetBalance ethGetBalance = connection
                .ethGetBalance(Wallet.getWallet().getAddress(), DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        return ethGetBalance.getBalance();
    }

    private static class WalletHolder {
        private static Wallet wallet = new Wallet();
    }

    public static Wallet getWallet(){
        return WalletHolder.wallet;
    }

    public String create(String password) throws ConnectionException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        /*
            returns credentials path

         */
        if(!Network.getNetwork().isConnected()){
            Network.getNetwork().connect();
        }

//        creating the wallet instance
        File internalStorage = MainActivity.getContext().getFilesDir();
        File path = new File(internalStorage.getPath() + "/wallets/");
        if(!path.exists()){
            path.mkdir();
        }
        String filename = WalletUtils.generateNewWalletFile(password, new File(internalStorage, "wallets"), false);
        String fullPath = internalStorage.getAbsolutePath() + "/wallets/" + filename;
        return fullPath;
    }

    public String getAddress(){
        return this.credentials.getAddress();
    }

    public ECKeyPair getEcKeyPair(){
        return this.credentials.getEcKeyPair();
    }

    public void unlockKeystore(String filePath, String password) throws IOException, CipherException, ConnectionException {
        if(!Network.getNetwork().isConnected()){
            Network.getNetwork().connect();
        }
        this.credentials = WalletUtils.loadCredentials(password, filePath);
    }

    public void unlockPrivateKey(String pk) throws ConnectionException {
        if(!Network.getNetwork().isConnected()){
            Network.getNetwork().connect();
        }
        this.credentials = Credentials.create(pk);
    }
}
