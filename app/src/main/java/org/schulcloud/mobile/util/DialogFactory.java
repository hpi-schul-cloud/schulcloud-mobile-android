package org.schulcloud.mobile.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import org.schulcloud.mobile.R;

public final class DialogFactory {

    public static Dialog createSimpleOkErrorDialogMultiLine(Context context, String title, String message) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(16);
        textView.setPadding(10, 5, 0, 0);
        textView.setText(title);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(textView)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return  alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static AlertDialog.Builder createSimpleOkCancelDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_action_cancel, null)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog;
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static AlertDialog.Builder createSingleSelectDialog(Context context, CharSequence[] singleChoiceItems, @StringRes int messageResource) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setSingleChoiceItems(singleChoiceItems, 0, null);
        alertDialog.setNegativeButton(R.string.dialog_action_cancel, null);
        alertDialog.setPositiveButton(R.string.dialog_action_ok, null);
        alertDialog.setTitle(context.getString(messageResource));
        return alertDialog;
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

}
