package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectoriesAdapter
        extends RecyclerView.Adapter<DirectoriesAdapter.DirectoriesViewHolder> {

    @Inject
    FilesPresenter mFilesPresenter;

    private List<Directory> mDirectories;
    private boolean mCanDeleteDirectories = false;

    @Inject
    public DirectoriesAdapter() {
        mDirectories = new ArrayList<>();
    }

    public void setDirectories(@NonNull List<Directory> directories) {
        mDirectories = directories;
        notifyDataSetChanged();
    }
    public void setCanDeleteDirectories(boolean canDeleteDirectories) {
        mCanDeleteDirectories = canDeleteDirectories;
        notifyItemRangeChanged(0, mDirectories.size());
    }

    @Override
    public DirectoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DirectoriesViewHolder holder = new DirectoriesViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_directory, parent, false));
        ViewUtil.setVisibility(holder.deleteDirectory, mCanDeleteDirectories);
        return holder;
    }

    @Override
    public void onBindViewHolder(DirectoriesViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Directory directory = mDirectories.get(position);

        holder.nameTextView.setText(directory.name);
        holder.cardView.setOnClickListener(v -> mFilesPresenter.onDirectorySelected(directory));
        holder.deleteDirectory.setOnClickListener(
                v -> DialogFactory.createSimpleOkCancelDialog(
                        context,
                        context.getString(R.string.files_directoryDelete_dialogTitle),
                        context.getString(R.string.files_directoryDelete_request, directory.name))
                        .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) ->
                                mFilesPresenter.onDirectoryDeleteSelected(directory))
                        .show());
    }

    @Override
    public int getItemCount() {
        return mDirectories.size();
    }

    class DirectoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.directory_delete_icon)
        AwesomeTextView deleteDirectory;

        public DirectoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
