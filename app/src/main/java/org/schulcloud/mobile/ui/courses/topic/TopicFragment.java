package org.schulcloud.mobile.ui.courses.topic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.main.MainFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicFragment extends MainFragment<TopicMvpView, TopicPresenter>
        implements TopicMvpView {
    private static final String ARGUMENT_TOPIC_ID = "ARGUMENT_TOPIC_ID";

    private String mTopicId = null;

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
     * @param topicId   The ID of the topic that should be shown
     * @param topicName The name of the topic that should be shown
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }


    /***** MVP View methods implementation *****/
    @Override
    public void showName(@NonNull String name) {
        topicName.setText(name);
    }
    @Override
    public void showContent(@NonNull List<Contents> contents) {
        mContentAdapter.setContent(contents);
        mContentAdapter.setContext(getActivity());
    }
}
