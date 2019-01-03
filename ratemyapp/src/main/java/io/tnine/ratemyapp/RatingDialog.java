package io.tnine.ratemyapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView default_app_icon;
    private RelativeLayout rate_dialog_bg;
    private int mColor = R.color.white;
    private int periodic_count = 3;
    private int icon;
    private String msg;
    private int cancel_txt_color = R.color.grey500;
    private int msg_txt_color = R.color.black;
    private int cancel_bg = 0;
    private Context context;
    private Activity activity;


    /**
     * init class object
     *
     * @return
     */
    public static RatingDialog getInstance() {
        if (instance == null) {
            instance = new RatingDialog();
        }
        return instance;
    }

    /**
     * set background color of rating dialog as you need
     *
     * @param color
     * @return default color is white
     */
    public RatingDialog setBackgroundColor(@ColorInt int color) {
        this.mColor = color;
        return this;
    }

    /**
     * set a number of app opening after which you want to show rating dialog
     *
     * @param periodic_count
     * @return otherwise it will take default value i.e. 3
     */
    public RatingDialog setPeriodicCount(int periodic_count) {
        this.periodic_count = periodic_count;
        return this;
    }

    /**
     * set your app icon to make it attractive
     *
     * @param icon
     * @return default icon will be star
     */
    public RatingDialog setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    /**
     * set a message with which you can attract your user to rate
     *
     * @param msg
     * @return
     */
    public RatingDialog setMessageText(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * set text color of cancel button
     *
     * @param cancel_txt_color
     * @return default color is grey
     */
    public RatingDialog setCancelTextColor(@ColorInt int cancel_txt_color) {
        this.cancel_txt_color = cancel_txt_color;
        return this;
    }

    /**
     * set background drawable for not now text
     * @param cancel_bg
     * @return
     */
    public RatingDialog setCancelTextBackground(@DrawableRes int cancel_bg) {
        this.cancel_bg = cancel_bg;
        return this;
    }

    public RatingDialog setMessageTextColor(@ColorInt int msg_txt_color){
        this.msg_txt_color = msg_txt_color;
        return this;
    }

    public RatingDialog setTextFont() {
        return this;
    }

    /**
     * initialize rating dialog
     *
     * @param context
     * @param activity
     * @return
     */
    public RatingDialog initDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        //--------------------------init local db------------------------
        new MyPref.Builder().setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (!MyPref.getBoolean(Constants.IS_DIALOG_INITIALIZED, false)) {
            MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
            MyPref.putBoolean(Constants.IS_DIALOG_INITIALIZED, true);
        }

        //-------------------------init dialog---------------------------
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.rate_dialog);

        //-----------------------views init------------------------
        default_rating_msg = dialog.findViewById(R.id.default_rating_msg);
        default_not_now = dialog.findViewById(R.id.default_not_now);
        ratingBar = dialog.findViewById(R.id.default_rate_bar);
        default_app_icon = dialog.findViewById(R.id.default_app_icon);
        rate_dialog_bg = dialog.findViewById(R.id.rate_dialog_bg);


        //-------------------------style views------------------------
        rate_dialog_bg.setBackgroundColor(context.getResources().getColor(mColor));
        default_app_icon.setImageDrawable(context.getResources().getDrawable(icon));
        default_not_now.setTextColor(context.getResources().getColor(cancel_txt_color));
        default_rating_msg.setTextColor(context.getResources().getColor(msg_txt_color));
        try {
            default_not_now.setBackground(context.getResources().getDrawable(cancel_bg));
        }catch (Exception e){
            e.printStackTrace();
        }
        default_rating_msg.setText(msg);

        return this;
    }

    public void showDialog() {
        Toast.makeText(context,String.valueOf(MyPref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0)),Toast.LENGTH_SHORT).show();
        if (!MyPref.getBoolean(Constants.IS_JOB_FINISHED, false)) {
            if (MyPref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0) == periodic_count) {
                showRatingDialog();
            }
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
                if (rating < 4) {
                    closeRatingDialog();
                    Toast.makeText(context, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeRatingDialog();
                            openProceedDialog(context, activity);
                        }
                    }, 500);
                }
            }
        });
    }

    private void closeRatingDialog() {
        dialog.dismiss();
        MyPref.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
    }

    private void showRatingDialog() {
        dialog.show();
    }

    private void openProceedDialog(final Context context, Activity activity) {
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
