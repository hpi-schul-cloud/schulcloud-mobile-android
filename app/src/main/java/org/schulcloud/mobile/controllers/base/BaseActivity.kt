package org.schulcloud.mobile.controllers.base

import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : AppCompatActivity() {
    protected fun setupActionBar() {
        if (toolbar != null)
            setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
