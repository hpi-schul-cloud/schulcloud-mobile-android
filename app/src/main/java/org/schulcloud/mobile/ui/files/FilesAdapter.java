package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.dialogs.SimpleDialogBuilder;

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
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);

        PopupMenu menu = new PopupMenu(context, view.findViewById(R.id.file_iv_overflow));
        menu.inflate(R.menu.item_file);
        menu.getMenu().findItem(R.id.files_file_action_delete)
                .setVisible(mCanDeleteFiles);

        FilesViewHolder holder = new FilesViewHolder(view, menu);
        ViewUtil.setVisibility(holder.vIv_overflow, mCanDeleteFiles);

        return holder;
    }
    @Override
    public void onBindViewHolder(FilesViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        File file = mFiles.get(position);
        PopupMenu menu = holder.nOverflow;

        holder.vEt_name.setText(file.name);
        holder.itemView.setOnClickListener(v -> mFilesPresenter.onFileSelected(file));
        holder.vAtv_download.setOnClickListener(v -> mFilesPresenter.onFileDownloadSelected(file));

        holder.vIv_overflow.setOnClickListener(v -> menu.show());
        holder.itemView.setOnLongClickListener(v -> {
            menu.show();
            return true;
        });
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.files_file_action_delete:
                    new SimpleDialogBuilder(context)
                            .title(R.string.files_fileDelete_dialogTitle)
                            .message(
                                    context.getString(R.string.files_fileDelete_request, file.name))
                            .buildAsSingle()
                            .subscribe(
                                    o -> mFilesPresenter.onFileDeleteSelected(file),
                                    throwable -> {}); // Ignore cancel event
                    return true;

                default:
                    return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    class FilesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView vEt_name;

        @BindView(R.id.file_atv_download)
        AwesomeTextView vAtv_download;
        @BindView(R.id.file_iv_overflow)
        View vIv_overflow;

        PopupMenu nOverflow;

        public FilesViewHolder(@NonNull View itemView, @NonNull PopupMenu menuOverflow) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            nOverflow = menuOverflow;
        }
    }
}
