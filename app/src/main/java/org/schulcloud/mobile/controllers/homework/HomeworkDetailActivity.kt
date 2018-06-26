package org.schulcloud.mobile.controllers.homework

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.viewmodels.HomeworkViewModel

class HomeworkDetailActivity : BaseActivity() {

    companion object {
        val TAG: String = HomeworkDetailActivity::class.java.simpleName
        const val EXTRA_ID = "EXTRA_ID"
    }

    private lateinit var homeworkViewModel: HomeworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_detail)

        homeworkViewModel = ViewModelProviders.of(this).get(HomeworkViewModel::class.java)
        homeworkViewModel.setHomeworkForId(intent.getStringExtra(EXTRA_ID))
    }
}
