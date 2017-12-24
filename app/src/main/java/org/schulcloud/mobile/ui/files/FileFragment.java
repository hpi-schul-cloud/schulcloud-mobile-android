package org.schulcloud.mobile.ui.files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.InternalFilesUtil;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;


public class FileFragment extends MainFragment implements FileMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    private static final int FILE_CHOOSE_RESULT_ACTION = 2017;
    private static final int FILE_READER_PERMISSION_CALLBACK_ID = 44;
    private static final int FILE_WRITER_PERMISSION_CALLBACK_ID = 43;

    @Inject
    FilePresenter mFilePresenter;

    @Inject
    FilesAdapter mFilesAdapter;
    @Inject
    DirectoriesAdapter mDirectoriesAdapter;

    private InternalFilesUtil mFilesUtil;
    private ProgressDialog mUploadProgressDialog;
    private ProgressDialog mDownloadProgressDialog;

    @BindView(R.id.files_breadcrumbs_v_color)
    View vV_breadcrumbs_color;
    @BindView(R.id.files_breadcrumbs_tv_text)
    TextView vTv_breadcrumbs_text;

    @BindView(R.id.directories_recycler_view)
    RecyclerView directoriesRecyclerView;
    @BindView(R.id.files_recycler_view)
    RecyclerView fileRecyclerView;
    @BindView(R.id.files_upload)
    FloatingActionButton fileUploadButton;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static FileFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    public static FileFragment newInstance(boolean triggerDataSyncOnCreate) {
        FileFragment fileFragment = new FileFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        fileFragment.setArguments(args);

        return fileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true)) {
            startService(FileSyncService.getStartIntent(getContext()));
            startService(DirectorySyncService.getStartIntent(getContext()));
        }

        mFilesUtil = new InternalFilesUtil(getActivity());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.files_title);

        fileRecyclerView.setAdapter(mFilesAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        directoriesRecyclerView.setAdapter(mDirectoriesAdapter);
        directoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fileUploadButton.setOnClickListener(v -> startFileChoosing());

        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(FileSyncService.getStartIntent(getContext()));
                    startService(DirectorySyncService.getStartIntent(getContext()));

                    new Handler().postDelayed(() -> {
                        mFilePresenter.loadFiles();
                        mFilePresenter.loadDirectories();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        mFilePresenter.attachView(this);
        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();

        return view;
    }
    @Override
    public void onPause() {
        mFilePresenter.detachView();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CHOOSE_RESULT_ACTION:
                if (data != null) {
                    java.io.File file = mFilesUtil.getFileFromContentPath(data.getData());
                    mFilePresenter.uploadFileToServer(file);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onBackPressed() {
        return mFilePresenter.stepOneDirectoryBack();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showBreadcrumbs(@NonNull String path, @Nullable Course course) {
        String[] folders = path.split("/");
        final StringBuilder currentPath = new StringBuilder(folders[0] + "/" + folders[1]);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Top-level directory ("Persönliche Dateien" or name and color of the course)
        if (course == null) {
            vV_breadcrumbs_color.setVisibility(View.GONE);
            builder.append(getString(R.string.filesOverview_my));
        } else {
            vV_breadcrumbs_color.setBackgroundColor(Color.parseColor(course.color));
            vV_breadcrumbs_color.setVisibility(View.VISIBLE);
            builder.append(course.name);
        }
        builder.setSpan(new BreadcrumbClickableSpan(currentPath.toString()),
                0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Add all subfolders
        for (int i = 2; i < folders.length; i++) {
            builder.append(getString(R.string.files_breadcrumbs_divider));
            currentPath.append("/").append(folders[i]);
            builder.append(folders[i]);
            builder.setSpan(new BreadcrumbClickableSpan(currentPath.toString()),
                    builder.length() - folders[i].length(), builder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        vTv_breadcrumbs_text.setText(builder);
        vTv_breadcrumbs_text.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private class BreadcrumbClickableSpan extends ClickableSpan {
        private final String mPath;

        BreadcrumbClickableSpan(@NonNull String path) {
            mPath = path;
        }

        @Override
        public void onClick(View widget) {
            mFilePresenter.goIntoDirectory(mPath);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    @Override
    public void showFiles(@NonNull List<File> files) {
        mFilesAdapter.setFiles(files);

        // adjust height of recycler view (bugfix for nested scrolling)
        ViewGroup.LayoutParams params = fileRecyclerView.getLayoutParams();
        params.height = 250 * mFilesAdapter.getItemCount() + 200;
        fileRecyclerView.setLayoutParams(params);
        fileRecyclerView.setNestedScrollingEnabled(false);
    }
    @Override
    public void showDirectories(@NonNull List<Directory> directories) {
        mDirectoriesAdapter.setDirectories(directories);
    }

    @Override
    public void showCanCreateFile(boolean canCreateFile) {
        ViewUtil.setVisibility(fileUploadButton, canCreateFile);
    }
    @Override
    public void showCanDeleteFiles(boolean canDeleteFiles) {
        mFilesAdapter.setCanDeleteFiles(canDeleteFiles);
    }
    @Override
    public void showCanDeleteDirectories(boolean canDeleteDirectories) {
        mDirectoriesAdapter.setCanDeleteDirectories(canDeleteDirectories);
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), getString(R.string.files_fetch_error))
                .show();
    }

    @Override
    public void showLoadingFileFromServerError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_load_error)
                .show();
    }

    @Override
    public void showFile(@NonNull String url, @NonNull String mimeType) {
        if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing())
            mDownloadProgressDialog.cancel();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(intent);
    }

    @Override
    public void showUploadFileError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_upload_error)
                .show();
    }

    @Override
    public void reloadFiles() {
        if (mUploadProgressDialog != null && mUploadProgressDialog.isShowing())
            mUploadProgressDialog.cancel();

        stopService(FileSyncService.getStartIntent(getContext()));
        stopService(DirectorySyncService.getStartIntent(getContext()));

        startService(FileSyncService.getStartIntent(getContext()));
        startService(DirectorySyncService.getStartIntent(getContext()));

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();
    }

    @Override
    public void saveFile(@NonNull ResponseBody body, @NonNull String fileName) {
        if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing())
            mDownloadProgressDialog.cancel();

        if (checkPermissions(FILE_WRITER_PERMISSION_CALLBACK_ID,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
            mFilesUtil.writeResponseBodyToDisk(body, fileName);
    }

    @Override
    public void startFileChoosing() {
        if (checkPermissions(FILE_READER_PERMISSION_CALLBACK_ID,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mUploadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                    R.string.files_upload_progress);
            mUploadProgressDialog.show();

            // show file chooser
            this.mFilesUtil.openFileChooser(FILE_CHOOSE_RESULT_ACTION);
        }
    }

    @Override
    public void startDownloading(@NonNull File file, boolean download) {
        mDownloadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                R.string.files_download_progress);
        mDownloadProgressDialog.show();
        mFilePresenter.loadFileFromServer(file, download);
    }

    /* File deletion */
    @Override
    public void startFileDeleting(@NonNull String path, @NonNull String fileName) {
        DialogFactory.createSimpleOkCancelDialog(
                getContext(),
                this.getResources().getString(R.string.files_dialog_delete_title),
                this.getResources().getString(R.string.files_delete_request, fileName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) ->
                        mFilePresenter.deleteFile(path))
                .show();
    }
    @Override
    public void showFileDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getResources().getString(R.string.files_delete_success_file),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        reloadFiles();
    }
    @Override
    public void showFileDeleteError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_delete_error_file)
                .show();
    }

    /* Directory deletion */
    @Override
    public void startDirectoryDeleting(@NonNull String path, @NonNull String dirName) {
        DialogFactory.createSimpleOkCancelDialog(
                getContext(),
                this.getResources().getString(R.string.files_dialog_delete_title),
                this.getResources().getString(R.string.files_delete_request, dirName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) ->
                        mFilePresenter.deleteDirectory(path))
                .show();
    }
    @Override
    public void showDirectoryDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getResources().getString(R.string.files_delete_success_directory),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        reloadFiles();
    }
    @Override
    public void showDirectoryDeleteError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_delete_error_directory)
                .show();
    }
}
