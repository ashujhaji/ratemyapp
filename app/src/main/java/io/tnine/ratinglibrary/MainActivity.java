package io.tnine.ratinglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.tnine.ratemyapp.RatingDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RatingDialog.getInstance()
                .setPeriodicCount(3)
                .setBackgroundColor(R.color.grey300)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setCancelTextColor(R.color.colorPrimary)
                .setMessageTextColor(R.color.colorPrimary)
                .setMessageText("Loved Rating App? Please rate us")
                .initDialog(MainActivity.this, MainActivity.this)
                .showDialog();
    }
}
