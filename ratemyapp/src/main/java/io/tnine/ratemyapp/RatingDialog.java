package io.tnine.ratemyapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.tnine.ratemyapp.utils.Constants;
import io.tnine.ratemyapp.utils.MyPref;

public class RatingDialog {

    private static RatingDialog instance;
    private static Dialog dialog;
    private TextView default_rating_msg, default_not_now;
    private RatingBar ratingBar;
    private static RelativeLayout rate_dialog_bg;


    public static RatingDialog getInstance(){
        if (instance == null){
            instance = new RatingDialog();
        }
        return instance;
    }

    public void openRatingDialog(final Context context, final Activity activity, String your_app_name){

        //------------------------shared preference init---------------------------
        new MyPref.Builder().setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (!MyPref.getBoolean(Constants.IS_DIALOG_INITIALIZED,true)){
            MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN,0);
        }

        //-------------------dialog init------------------------------
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.rate_dialog);

        //-----------------------views init------------------------
        default_rating_msg = dialog.findViewById(R.id.default_rating_msg);
        default_not_now = dialog.findViewById(R.id.default_not_now);
        ratingBar = dialog.findViewById(R.id.default_rate_bar);


        //----------------------------------set view data------------------------
        default_rating_msg.setText("Are you enjoying "+your_app_name+" ? Please rate us");

        if (MyPref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN,0)==3){
            showRatingDialog();
        }

        default_not_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRatingDialog();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeRatingDialog();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating<4){
                    closeRatingDialog();
                    Toast.makeText(context, "Thanks for your feedback",Toast.LENGTH_SHORT).show();
                }else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeRatingDialog();
                            openProceedDialog(context,activity);
                        }
                    },500);
                }
            }
        });

        if (!MyPref.getBoolean(Constants.IS_JOB_FINISHED,true)){
            if (MyPref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN,0)==3){
                dialog.show();
            }
        }
    }

    private void closeRatingDialog(){
        dialog.dismiss();
        MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN,0);
    }

    private void showRatingDialog(){
        dialog.show();
        MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN,0);
    }

    private void openProceedDialog(final Context context, Activity activity){
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_proceed_dialog, null);
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        TextView btn_positive = dialogView.findViewById(R.id.proceed_positive_btn);
        TextView btn_negative = dialogView.findViewById(R.id.proceed_neutral_btn);

        // Create the alert dialog
        final AlertDialog alertDialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                alertDialog.dismiss();
                Uri uri = Uri.parse("market://details?id=" + context.getApplicationContext().getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context.getApplicationContext(), " unable to find market app", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
                MyPref.putBoolean(Constants.IS_JOB_FINISHED, true);
                MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
            }
        });
        // Display the custom alert dialog on interface
        alertDialog.show();
    }
}
