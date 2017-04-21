package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by niklaskiefer on 21.04.17.
 */

public class FileActivity extends BaseActivity implements FileMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.FileActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    FilePresenter mFilePresenter;
    @Inject
    FilesAdapter mFilesAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setAdapter(mFilesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFilePresenter.attachView(this);
        mFilePresenter.loadFiles();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(FileSyncService.getStartIntent(this));
        }
    }

    @Override
    protected void onDestroy() {
        mFilePresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showFiles(List<File> files) {
        mFilesAdapter.setFiles(files);
        mFilesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_files_fetch))
                .show();
    }
}
