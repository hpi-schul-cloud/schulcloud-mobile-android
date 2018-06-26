package org.schulcloud.mobile.controllers.homework

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.HomeworkViewModelFactory
import kotlinx.android.synthetic.main.activity_homework_detail.*

class HomeworkDetailActivity : BaseActivity() {

    companion object {
        val TAG: String = HomeworkDetailActivity::class.java.simpleName
        const val EXTRA_ID = "EXTRA_ID"
    }

    private lateinit var homeworkViewModel: HomeworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_detail)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        homeworkViewModel = ViewModelProviders.of(this, HomeworkViewModelFactory(intent.getStringExtra(EXTRA_ID))).get(HomeworkViewModel::class.java)
        homeworkViewModel.getHomework().observe(this, Observer<Homework>{
            it?.let{onHomeworkUpdate(it)}
        })
    }

    private fun onHomeworkUpdate(homework: Homework){
        homework_detail_title.text = homework.title

        homework.courseId?.let {
            homework_detail_course_title.text = it.name
            homework_detail_course_color.setColorFilter(Color.parseColor(it.color))
        }

        homework.dueDate?.let{
            val dueTextAndColorId: Pair<String, Int>?
            dueTextAndColorId = homework.getDueTextAndColorId()
            homework_detail_duetill.text = dueTextAndColorId.first
            homework_detail_duetill.setTextColor(dueTextAndColorId.second)

        }
        // TODO: improve HMTL text appearance
        homework.description?.let{
            homework_detail_description.loadData(it, "text/html", null)
        }
        homework_detail_description.setBackgroundColor(ContextCompat.getColor(this, R.color.background_main))
    }
}
