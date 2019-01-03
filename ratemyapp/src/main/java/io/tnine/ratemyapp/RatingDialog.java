package io.tnine.ratemyapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

public class RatingDialog {

    private static RatingDialog instance;
    private Dialog dialog;
    private TextView default_rating_msg, default_not_now;

    public static RatingDialog getInstance(){
        if (instance == null){
            instance = new RatingDialog();
        }
        return instance;
    }

    public void openRatingDialog(Context context, Activity activity, String your_app_name){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.rate_dialog);

        default_rating_msg = dialog.findViewById(R.id.default_rating_msg);
        default_not_now = dialog.findViewById(R.id.default_not_now);

        default_rating_msg.setText("Are you enjoying "+your_app_name+" ? Please rate us");

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

        dialog.show();
    }

    private void closeRatingDialog(){
        dialog.dismiss();
    }
}
