package io.tnine.ratemyapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.tnine.ratemyapp.utils.Constants;

public class RatingDialog {

    private static RatingDialog instance;
    private static Dialog dialog;
    private TextView default_not_now;
    private TextView default_never;
    private RatingBar ratingBar;
    private int mColor = R.color.white;
    private int periodic_count = 3;
    private int icon = R.drawable.star;
    private String msg;
    private int cancel_txt_color = R.color.grey500;
    private int msg_txt_color = R.color.black;
    private int cancel_bg = R.drawable.star;
    private int proceed_bg = R.drawable.bg_proceed;
    private Context context;
    private Activity activity;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


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
    public RatingDialog setBackgroundColor(int color) {
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
    public RatingDialog setCancelTextColor(int cancel_txt_color) {
        this.cancel_txt_color = cancel_txt_color;
        return this;
    }

    /**
     * set background drawable for not now text
     *
     * @param cancel_bg
     * @return
     */
    public RatingDialog setCancelTextBackground(int cancel_bg) {
        this.cancel_bg = cancel_bg;
        return this;
    }

    /**
     * set message text color
     * @param msg_txt_color
     * @return
     */
    public RatingDialog setMessageTextColor(int msg_txt_color) {
        this.msg_txt_color = msg_txt_color;
        return this;
    }

    /**
     * set background drawable to proceed button
     * @param proceedBg
     * @return
     */
    public RatingDialog setProceedBackgroundDrawable(int proceedBg){
        this.proceed_bg = proceedBg;
        return this;
    }

//    public RatingDialog setTextFont() {
//        return this;
//    }

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
        pref = context.getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE);
        editor = pref.edit();

        if (!pref.getBoolean(Constants.IS_DIALOG_INITIALIZED, false)) {
            editor.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
            editor.putBoolean(Constants.IS_DIALOG_INITIALIZED, true);
            editor.apply();
        }

        //-------------------------init dialog---------------------------
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.rate_dialog);

        //-----------------------views init------------------------
        TextView default_rating_msg = dialog.findViewById(R.id.default_rating_msg);
        default_not_now = dialog.findViewById(R.id.default_not_now);
        default_never = dialog.findViewById(R.id.default_never);
        ratingBar = dialog.findViewById(R.id.default_rate_bar);
        ImageView default_app_icon = dialog.findViewById(R.id.default_app_icon);
        RelativeLayout rate_dialog_bg = dialog.findViewById(R.id.rate_dialog_bg);


        //-------------------------style views------------------------
        rate_dialog_bg.setBackgroundColor(context.getResources().getColor(mColor));
        default_app_icon.setImageDrawable(context.getResources().getDrawable(icon));
        default_not_now.setTextColor(context.getResources().getColor(cancel_txt_color));
        default_rating_msg.setTextColor(context.getResources().getColor(msg_txt_color));
        default_not_now.setBackground(context.getResources().getDrawable(cancel_bg));
        default_rating_msg.setText(msg);

        return this;
    }

    public void showDialog() {
        if (!pref.getBoolean(Constants.IS_JOB_FINISHED, false)) {
            editor.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, pref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0) + 1);
            editor.apply();
            if (pref.getInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0) == periodic_count) {
                showRatingDialog();
            }
        }

        default_not_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRatingDialog();
            }
        });

        default_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRatingDialog();
                editor.putBoolean(Constants.IS_JOB_FINISHED,true);
                editor.apply();
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
        editor.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
        editor.apply();
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

        btn_positive.setBackground(context.getResources().getDrawable(proceed_bg));
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
                editor.putBoolean(Constants.IS_JOB_FINISHED, true);
                editor.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
                editor.apply();
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                editor.putInt(Constants.COUNTS_FOR_DIALOG_OPEN, 0);
                editor.apply();
            }
        });
        // Display the custom alert dialog on interface
        alertDialog.show();
    }
}
