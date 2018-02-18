package org.schulcloud.mobile.ui.courses.topic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicFragment extends MainFragment<TopicMvpView, TopicPresenter>
        implements TopicMvpView {
    private static final String ARGUMENT_TOPIC_ID = "ARGUMENT_TOPIC_ID";

    @Inject
    TopicPresenter mTopicPresenter;

    @Inject
    ContentAdapter mContentAdapter;

    @BindView(R.id.topicName)
    TextView topicName;
    @BindView(R.id.topicRecycler)
    RecyclerView recyclerView;

    /**
     * Creates a new instance of this fragment.
     *
     * @param topicId The ID of the topic that should be shown
     * @return The new instance
     */
    @NonNull
    public static TopicFragment newInstance(@NonNull String topicId) {
        TopicFragment topicFragment = new TopicFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_TOPIC_ID, topicId);
        topicFragment.setArguments(args);

        return topicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mTopicPresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        mTopicPresenter.loadContents(args.getString(ARGUMENT_TOPIC_ID));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.courses_topic_title);

        recyclerView.setAdapter(mContentAdapter);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int spans = metrics.widthPixels / ViewUtil.dpToPx(440);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(spans, StaggeredGridLayoutManager.VERTICAL);
        layoutManager
                .setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }


    /***** MVP View methods implementation *****/
    @Override
    public void showName(@NonNull String name) {
        topicName.setText(name);
    }
    @Override
    public void showContent(@NonNull List<Contents> contents) {
        mContentAdapter.setContents(contents);
    }
}
