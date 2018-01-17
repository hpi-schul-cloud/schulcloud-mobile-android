package org.schulcloud.mobile.ui.feedback;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Toast;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackDialog extends BaseDialog<FeedbackMvpView, FeedbackPresenter>
        implements FeedbackMvpView {
    private static final String ARGUMENT_CONTEXT_NAME = "contextName";

    @Inject
    FeedbackPresenter mFeedbackPresenter;

    @BindView(R.id.email)
    TextInputEditText mEmailInput;
    @BindView(R.id.opinion)
    TextInputEditText mOpinionInput;
    @BindView(R.id.opinionWrapper)
    TextInputLayout mOpinionWrapper;

    @NonNull
    public static FeedbackDialog newInstance(@NonNull String contextName) {
        FeedbackDialog feedbackDialog = new FeedbackDialog();

        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_CONTEXT_NAME, contextName);
        feedbackDialog.setArguments(arguments);

        return feedbackDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mFeedbackPresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        mFeedbackPresenter.init(args.getString(ARGUMENT_CONTEXT_NAME));
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        ButterKnife.bind(this, view);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.feedback_title)
                .setView(view)
                .setPositiveButton(R.string.feedback_send, null)
                .setNegativeButton(R.string.dialog_action_cancel, null).create();
        dialog.setOnShowListener(dialog1 ->
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener(v ->
                                mFeedbackPresenter.sendFeedback(
                                        getString(R.string.feedback_transmit_format),
                                        mEmailInput.getText().toString(),
                                        mOpinionInput.getText().toString(),
                                        getString(R.string.feedback_transmit_subject),
                                        getString(R.string.feedback_transmit_to))));
        return dialog;
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showError_contentEmpty() {
        mOpinionWrapper.setError(getString(R.string.feedback_error_opinionEmpty));
    }
    @Override
    public void showFeedbackSent() {
        Toast.makeText(getContext(), R.string.feedback_transmit_sent, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
