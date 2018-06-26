package org.schulcloud.mobile.controllers.homework

import android.app.Activity
import android.os.Bundle
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity

class HomeworkDetailActivity : BaseActivity() {

    companion object {
        val TAG: String = HomeworkDetailActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_detail)
    }
}
