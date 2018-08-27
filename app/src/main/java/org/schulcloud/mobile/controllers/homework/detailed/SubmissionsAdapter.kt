package org.schulcloud.mobile.controllers.homework.detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemSubmissionBinding
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.user.User


class SubmissionsAdapter(private val onSelected: (User) -> Unit) :
        BaseAdapter<Pair<User, Submission?>, SubmissionsAdapter.SubmissionViewHolder, ItemSubmissionBinding>() {

    private lateinit var homework: Homework
    private var selectedIndex = -1

    fun update(
        homework: Homework,
        submissions: List<Pair<User, Submission?>>,
        selectedUserId: String?
    ) {
        this.homework = homework
        items = submissions
        selectedIndex = submissions.indexOfFirst { (user, _) -> user.id == selectedUserId }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val binding = ItemSubmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubmissionViewHolder(binding)
    }


    inner class SubmissionViewHolder(binding: ItemSubmissionBinding) :
            BaseViewHolder<Pair<User, Submission?>, ItemSubmissionBinding>(binding) {

        init {
            binding.wrapper.setOnClickListener {
                mark()
                onSelected(item.first)
            }
        }

        override fun onItemSet() {
            binding.student = item.first
            binding.submission = item.second
            if (selectedIndex == adapterPosition)
                mark()
        }

        private fun mark() {
            // un-mark previous
            if (selectedIndex >= 0) {
                val previous = recyclerView
                        ?.findViewHolderForAdapterPosition(selectedIndex) as? SubmissionViewHolder
                previous?.binding?.wrapper?.setBackground(null)
            }

            // mark
            selectedIndex = adapterPosition
            binding.wrapper.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_dark))
        }
    }
}
