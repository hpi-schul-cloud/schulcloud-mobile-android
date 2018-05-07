package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.ui.base.BaseViewHolder;

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
    private FragmentManager mFragmentManager;
    private Homework mHomework;
    private String mCurrentUserId;
    private List<Pair<User, Submission>> mSubmissions;
    private int mExpandedSubmission = -1;

    @Inject
    public SubmissionsAdapter() {
        mSubmissions = Collections.emptyList();
    }
    public void init(@NonNull FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
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

    class SubmissionViewHolder extends BaseViewHolder<Pair<User, Submission>>
            implements ExpandableLayout.OnExpansionUpdateListener {
        @BindView(R.id.submission_ll_header)
        LinearLayout vLl_header;
        @BindView(R.id.submission_tv_name)
        TextView vTv_name;
        @BindView(R.id.submission_iv_submitted)
        ImageView vIv_submitted;
        @BindView(R.id.submission_tv_grade)
        TextView vTv_grade;
        @BindView(R.id.submission_iv_expand)
        ImageView vIv_expanded;

        @BindView(R.id.submission_expansion)
        ExpandableLayout vExpansion;
        @BindView(R.id.submission_tabs)
        TabLayout vTabs;
        @BindView(R.id.submission_vp_content)
        ViewPager vVp_content;

        EvaluationPagerAdapter mAdapter;

        SubmissionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            vLl_header.setOnClickListener(v -> {
                SubmissionViewHolder expandedHolder = (SubmissionViewHolder) getRecyclerView()
                        .findViewHolderForAdapterPosition(mExpandedSubmission);
                if (expandedHolder != null)
                    expandedHolder.collapse();

                int position = getAdapterPosition();
                if (position == mExpandedSubmission)
                    mExpandedSubmission = -1;
                else {
                    expand();
                    mExpandedSubmission = position;
                }
            });

            mAdapter = new EvaluationPagerAdapter(getContext(), mFragmentManager);
            vVp_content.setOffscreenPageLimit(2); // Maximum: 3 Tabs
            vVp_content.setAdapter(mAdapter);
            vTabs.setupWithViewPager(vVp_content);
        }
        @Override
        protected void onItemSet(@NonNull Pair<User, Submission> item) {
            User user = item.first;
            Submission submission = item.second;
            vTv_name.setText(!TextUtils.isEmpty(user.displayName) ? user.displayName : getContext()
                    .getString(R.string.homework_detailed_submissions_submission_name,
                            user.firstName, user.lastName));
            vIv_submitted.setImageResource(submission != null
                    ? R.drawable.ic_check_green_24dp
                    : R.drawable.ic_close_red_24dp);
            vTv_grade.setText((submission != null && submission.grade != null)
                    ? getContext().getString(R.string.homework_submission_grade, submission.grade)
                    : "");

            boolean expanded = getAdapterPosition() == mExpandedSubmission;
            vExpansion.setExpanded(expanded, false);
            updateExpandedIndicator(expanded);

            mAdapter.setSubmission(mCurrentUserId, user, mHomework, submission);
            mAdapter.notifyDataSetChanged();
        }

        void expand() {
            vExpansion.expand();
            updateExpandedIndicator(true);
        }
        void collapse() {
            vExpansion.collapse();
            updateExpandedIndicator(false);
        }
        private void updateExpandedIndicator(boolean expanded) {
            vIv_expanded.setImageResource(expanded
                    ? R.drawable.ic_expand_less_dark_24dp
                    : R.drawable.ic_expand_more_dark_24dp);
        }
        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDING)
                getRecyclerView().smoothScrollToPosition(getAdapterPosition());
        }
    }
}
