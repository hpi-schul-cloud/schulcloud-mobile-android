package org.schulcloud.mobile.controllers.file

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity

class FileChooseActivtiy: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_file)
    }
}
