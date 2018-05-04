package org.schulcloud.mobile.ui.homework.detailed.details;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.common.ContentWebView;
import org.schulcloud.mobile.ui.main.MainFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 4/27/2018
 */
public class DetailsFragment extends MainFragment<DetailsMvpView, DetailsPresenter>
        implements DetailsMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";

    @Inject
    DetailsPresenter mPresenter;
    @BindView(R.id.homeworkDetailedDetails_cwv_content)
    ContentWebView vCwv_content;

    @NonNull
    public static DetailsFragment newInstance(@NonNull String homeworkId) {
        DetailsFragment detailsFragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mPresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        String id = getArguments().getString(ARGUMENT_HOMEWORK_ID);
        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        mPresenter.loadHomework(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_detailed_details, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    /* MVP View methods implementation */
    @Override
    public void showDescription(@NonNull String description) {
        vCwv_content.setContent(description);
    }
}
