package com.wallthereum.wallthereum;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {

    private TextView mLicenceText;
    private TextView mVersionText;
    private String mGithubRepositoryAddress = "https://github.com/Msiavashi/wallthereum-android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        this.mLicenceText = findViewById(R.id.license_text_view);
        this.mVersionText = findViewById(R.id.version_text_view);
        initTextViews();
    }

    private void initTextViews() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            mVersionText.setText(getResources().getString(R.string.version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mLicenceText.setText(R.string.license_description);
    }


    public void githubButtonOnClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mGithubRepositoryAddress));
        startActivity(browserIntent);
    }
}
