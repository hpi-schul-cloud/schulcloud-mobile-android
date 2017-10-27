package org.schulcloud.mobile.ui.files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.InternalFilesUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;


public class FileFragment extends MainFragment implements FileMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.FileFragment.EXTRA_TRIGGER_SYNC_FLAG";

    private static final int FILE_CHOOSE_RESULT_ACTION = 2017;
    private static final int FILE_READER_PERMISSION_CALLBACK_ID = 44;
    private static final int FILE_WRITER_PERMISSION_CALLBACK_ID = 43;

    @Inject
    FilePresenter mFilePresenter;

    @Inject
    FilesAdapter mFilesAdapter;

    @Inject
    DirectoriesAdapter mDirectoriesAdapter;

    @BindView(R.id.directories_recycler_view)
    RecyclerView directoriesRecyclerView;

    @BindView(R.id.files_recycler_view)
    RecyclerView fileRecyclerView;

    @BindView(R.id.files_upload)
    FloatingActionButton fileUploadButton;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    private InternalFilesUtil filesUtil;
    private ProgressDialog uploadProgressDialog;
    private ProgressDialog downloadProgressDialog;

    public static FileFragment getInstance() {
        return getInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    public static FileFragment getInstance(boolean triggerDataSyncOnCreate) {
        FileFragment fileFragment = new FileFragment();

        Bundle args = new Bundle();
        args.putBoolean(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        fileFragment.setArguments(args);

        return fileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        mFilePresenter.attachView(this);

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();

        filesUtil = new InternalFilesUtil(getContext());

        if (getArguments().getBoolean(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(FileSyncService.getStartIntent(getContext()));
            startService(DirectorySyncService.getStartIntent(getContext()));
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_files, container, false);
        ButterKnife.bind(this, view);
        getMainActivity().setTitle(R.string.files_title);

        fileRecyclerView.setAdapter(mFilesAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        directoriesRecyclerView.setAdapter(mDirectoriesAdapter);
        directoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fileUploadButton.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.hpiRed)));
        fileUploadButton.setOnClickListener(v -> {
            startFileChoosing();
        });

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed),
                getResources().getColor(R.color.hpiOrange),
                getResources().getColor(R.color.hpiYellow));
        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(FileSyncService.getStartIntent(getContext()));
                    startService(DirectorySyncService.getStartIntent(getContext()));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mFilePresenter.loadFiles();
                        mFilePresenter.loadDirectories();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        return view;
    }

    @Override
    public void onDestroy() {
        mFilePresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CHOOSE_RESULT_ACTION:
                if (data != null) {
                    java.io.File file = filesUtil.getFileFromContentPath(data.getData());
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
    public void showFiles(List<File> files) {
        mFilesAdapter.setFiles(files);
        mFilesAdapter.notifyDataSetChanged();

        // adjust height of recycler view (bugfix for nested scrolling)
        ViewGroup.LayoutParams params = fileRecyclerView.getLayoutParams();
        params.height = 250 * mFilesAdapter.getItemCount() + 200;
        fileRecyclerView.setLayoutParams(params);
        fileRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void showDirectories(List<Directory> directories) {
        mDirectoriesAdapter.setDirectories(directories);
        mDirectoriesAdapter.notifyDataSetChanged();
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
    public void showFile(String url, String mimeType) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing())
            downloadProgressDialog.cancel();

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
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing())
            uploadProgressDialog.cancel();

        stopService(FileSyncService.getStartIntent(getContext()));
        stopService(DirectorySyncService.getStartIntent(getContext()));

        startService(FileSyncService.getStartIntent(getContext()));
        startService(DirectorySyncService.getStartIntent(getContext()));

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();
    }

    @Override
    public void saveFile(ResponseBody body, String fileName) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing())
            downloadProgressDialog.cancel();

        if (checkPermissions(
                FILE_WRITER_PERMISSION_CALLBACK_ID,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.filesUtil.writeResponseBodyToDisk(body, fileName);
        }
    }

    @Override
    public void startFileChoosing() {
        if (checkPermissions(
                FILE_READER_PERMISSION_CALLBACK_ID,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {


            uploadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                    R.string.files_upload_progress);
            uploadProgressDialog.show();

            // show file chooser
            this.filesUtil.openFileChooser(FILE_CHOOSE_RESULT_ACTION);
        }
    }

    @Override
    public void startDownloading(File file, Boolean download) {
        downloadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                R.string.files_download_progress);
        downloadProgressDialog.show();
        mFilePresenter.loadFileFromServer(file, download);
    }

    @Override
    public void startFileDeleting(String path, String fileName) {
        DialogFactory.createSimpleOkCancelDialog(
                getContext(),
                this.getResources().getString(R.string.files_dialog_delete_title),
                this.getResources().getString(R.string.files_delete_request, fileName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) -> {
                    mFilePresenter.deleteFile(path);
                })
                .show();
    }

    @Override
    public void showFileDeleteError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_delete_error)
                .show();
    }

    @Override
    public void startDirectoryDeleting(String path, String dirName) {
        DialogFactory.createSimpleOkCancelDialog(
                getContext(),
                this.getResources().getString(R.string.files_dialog_delete_title),
                this.getResources().getString(R.string.files_delete_request, dirName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) -> {
                    mFilePresenter.deleteDirectory(path);
                })
                .show();
    }

    public void showFileDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getResources().getString(R.string.files_delete_success_file),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        this.reloadFiles();
    }

    @Override
    public void showDirectoryDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getResources().getString(R.string.files_delete_success_directory),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        this.reloadFiles();
    }
}
