package com.hrishi3331studio.template_informal.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class LoaderDialog {

    Context context;
    private ProgressDialog dialog;

    public LoaderDialog(final Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Loading data..");
        dialog.setMessage("Please wait");
    }

    public void showLoader(){
        dialog.show();
    }

    public void dismissLoader(){
        dialog.dismiss();
    }

}
