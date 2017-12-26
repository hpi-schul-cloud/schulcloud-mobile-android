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
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    @Inject
    FilesPresenter mFilesPresenter;

    private List<File> mFiles;
    private boolean mCanDeleteFiles;

    @Inject
    public FilesAdapter() {
        mFiles = new ArrayList<>();
    }

    public void setFiles(@NonNull List<File> files) {
        mFiles = files;
        notifyDataSetChanged();
    }
    public void setCanDeleteFiles(boolean canDeleteFiles) {
        mCanDeleteFiles = canDeleteFiles;
        notifyItemRangeChanged(0, mFiles.size());
    }

    @Override
    public FilesAdapter.FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FilesViewHolder holder = new FilesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false));
        ViewUtil.setVisibility(holder.deleteIcon, mCanDeleteFiles);
        return holder;
    }

    @Override
    public void onBindViewHolder(FilesAdapter.FilesViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        File file = mFiles.get(position);

        holder.nameTextView.setText(file.name);
        holder.cardView.setOnClickListener(v -> mFilesPresenter.onFileSelected(file));
        holder.downloadIcon.setOnClickListener(v -> mFilesPresenter.onFileDownloadSelected(file));

        holder.deleteIcon.setOnClickListener(
                v -> DialogFactory.createSimpleOkCancelDialog(
                        context,
                        context.getString(R.string.files_fileDelete_dialogTitle),
                        context.getString(R.string.files_fileDelete_request, file.name))
                        .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) ->
                                mFilesPresenter.onFileDeleteSelected(file))
                        .show());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    class FilesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.file_download_icon)
        AwesomeTextView downloadIcon;
        @BindView(R.id.file_delete_icon)
        AwesomeTextView deleteIcon;

        public FilesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
