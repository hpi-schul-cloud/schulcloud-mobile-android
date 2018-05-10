package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.ui.base.BaseViewHolder;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.util.ModelUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 5/5/2018
 */
@ConfigPersistent
public class SubmissionsAdapter extends BaseAdapter<SubmissionsAdapter.SubmissionViewHolder> {
    private Homework mHomework;
    private String mCurrentUserId;
    private List<Pair<User, Submission>> mSubmissions;

    @Inject
    public SubmissionsAdapter() {
        mSubmissions = Collections.emptyList();
    }
    public void setSubmissions(@NonNull String currentUserId, @NonNull Homework homework,
            @NonNull List<Pair<User, Submission>> submissions) {
        if (TextUtils.equals(mCurrentUserId, currentUserId) && mHomework == homework
                && mSubmissions == submissions)
            return;

        mCurrentUserId = currentUserId;
        mHomework = homework;
        mSubmissions = submissions;
        notifyDataSetChanged();
    }
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mSubmissions.clear();
        notifyDataSetChanged();
        super.onDetachedFromRecyclerView(recyclerView);
    }
    @Override
    public SubmissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubmissionViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_submission, parent, false));
    }

    @Override
    public void onBindViewHolder(SubmissionViewHolder holder, int position) {
        holder.setItem(mSubmissions.get(position));
    }
    @Override
    public int getItemCount() {
        return mSubmissions.size();
    }

    class SubmissionViewHolder extends BaseViewHolder<Pair<User, Submission>> {

        @BindView(R.id.submission_ll_wrapper)
        LinearLayout vLl_header;
        @BindView(R.id.submission_tv_name)
        TextView vTv_name;
        @BindView(R.id.submission_iv_submitted)
        ImageView vIv_submitted;
        @BindView(R.id.submission_tv_grade)
        TextView vTv_grade;

        SubmissionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            vLl_header.setOnClickListener(v -> {
                String studentId = mSubmissions.get(getAdapterPosition()).first._id;
                getMainActivity().addFragment(
                        DetailedHomeworkFragment.newInstance(mHomework._id, studentId));
            });
        }
        @Override
        protected void onItemSet(@NonNull Pair<User, Submission> item) {
            User user = item.first;
            Submission submission = item.second;
            vTv_name.setText(ModelUtil.getUserName(getContext(), user));
            vIv_submitted.setImageResource(submission != null
                    ? R.drawable.ic_check_green_24dp
                    : R.drawable.ic_close_red_24dp);
            vTv_grade.setText((submission != null && submission.grade != null)
                    ? getContext().getString(R.string.homework_submission_grade, submission.grade)
                    : "");
        }
    }
}
