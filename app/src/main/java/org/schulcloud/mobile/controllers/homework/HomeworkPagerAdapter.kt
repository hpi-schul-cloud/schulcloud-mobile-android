package org.schulcloud.mobile.controllers.homework

import android.content.Context
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository


class HomeworkPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        private const val TAB_INVALID = 0
        private const val TAB_DETAILS = 1
        private const val TAB_SUBMISSION = 2
        private const val TAB_FEEDBACK = 3
        private const val TAB_SUBMISSIONS = 4

        @Retention(AnnotationRetention.SOURCE)
        @MustBeDocumented
        @IntDef(TAB_INVALID, TAB_DETAILS, TAB_SUBMISSION, TAB_FEEDBACK, TAB_SUBMISSIONS)
        annotation class Tab
    }

    private lateinit var homework: Homework


    private var studentId: String? = null

    fun setHomework(homework: Homework, student: User?) {
        this.homework = homework

        if (student != null)
            studentId = student.id
        else if (!homework.isTeacher(UserRepository.userId!!))
            studentId = UserRepository.userId
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment? {
        return when (getTabType(position)) {
            TAB_DETAILS -> OverviewFragment()
            TAB_SUBMISSION -> SubmissionFragment()
            TAB_FEEDBACK -> OverviewFragment()
            TAB_SUBMISSIONS -> SubmissionsFragment()

            TAB_INVALID -> null
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val titleId = when (getTabType(position)) {
            TAB_DETAILS -> R.string.homework_details
            TAB_SUBMISSION -> R.string.homework_submission
            TAB_FEEDBACK -> R.string.homework_feedback
            TAB_SUBMISSIONS -> R.string.homework_submissions

            TAB_INVALID -> return null
            else -> return null
        }
        return context.getString(titleId)
    }

    override fun getItemPosition(obj: Any): Int {
        (obj as? StudentDependentFragment)?.update(studentId!!)
        return POSITION_UNCHANGED
    }

    override fun getCount(): Int {
        var count = 0
        while (getTabType(count++) != TAB_INVALID);
        return count - 1
    }

    @Tab
    private fun getTabType(position: Int): Int {
        if (!::homework.isInitialized)
            return TAB_INVALID

        return when {
            position == 0 -> TAB_DETAILS
            homework.publicSubmissions || homework.isTeacher(UserRepository.userId!!) -> {
                when {
                    position == 1 -> TAB_SUBMISSIONS
                    studentId != null && position == 2 -> TAB_SUBMISSION
                    studentId != null && position == 3 -> TAB_FEEDBACK
                    else -> TAB_INVALID
                }
            }
            position == 1 -> TAB_SUBMISSION
            position == 2 -> TAB_FEEDBACK
            position == 3 -> TAB_SUBMISSIONS
            else -> TAB_INVALID
        }
    }
}
