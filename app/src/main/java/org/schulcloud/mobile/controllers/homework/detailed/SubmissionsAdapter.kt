package org.schulcloud.mobile.controllers.homework.detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemSubmissionBinding
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.user.User


class SubmissionsAdapter(private val onSelected: (String) -> Unit) :
        BaseAdapter<Pair<User, Submission?>, SubmissionsAdapter.SubmissionViewHolder, ItemSubmissionBinding>() {

    fun update(submissions: List<Pair<User, Submission?>>) {
        items = submissions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val binding = ItemSubmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return SubmissionViewHolder(binding)
    }


    inner class SubmissionViewHolder(binding: ItemSubmissionBinding) :
            BaseViewHolder<Pair<User, Submission?>, ItemSubmissionBinding>(binding) {
        override fun onItemSet() {
            binding.student = item.first
            binding.submission = item.second
        }
    }
}
