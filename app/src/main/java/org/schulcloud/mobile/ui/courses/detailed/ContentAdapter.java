package org.schulcloud.mobile.ui.courses.detailed;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<Contents> mContent;
    private String mUserId;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    public ContentAdapter() {
        mContent = new ArrayList<>();
    }

    public void setContent(List<Contents> content) {
        mContent = content;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        Contents contents = mContent.get(position);
        holder.nameTextView.setText(contents.title);

        if (contents.component.equals("text"))
            holder.descriptionTextView.setText(Html.fromHtml(contents.content.text));
        else
            holder.descriptionTextView.setText(contents.component + " wird zurzeit nicht unterstüzt.");
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.text_description)
        TextView descriptionTextView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
