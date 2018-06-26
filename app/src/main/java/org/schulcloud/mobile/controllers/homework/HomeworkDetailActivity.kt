package org.schulcloud.mobile.controllers.homework

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
        // TODO: handle images in HTML code
        homework.description?.let{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
               homework_detail_description.text  = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
            }
            else{
                @Suppress("DEPRECATION")
                homework_detail_description.text = Html.fromHtml(it)
            }

            homework_detail_description.text =   homework_detail_description.text.toString().trim()
        }
    }
}
