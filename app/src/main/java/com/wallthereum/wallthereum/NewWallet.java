//TODO: add password validation

package com.wallthereum.wallthereum;

public class NewWallet {
    private String mPassword;


    public NewWallet(String password){
        this.mPassword = password;
    }

    public void showWarningDialog() {

    }

    public void onWarningDeclined() {

    }

    public void onWarningAccepted() {

    }

    public void create() {

    }
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String Password) {
        this.mPassword = mPassword;
    }
}
