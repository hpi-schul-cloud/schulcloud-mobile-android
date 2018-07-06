package org.schulcloud.mobile.controllers.main

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemFileCourseBinding
import org.schulcloud.mobile.models.course.Course

class FileOverviewCourseAdapter(private val selectedCallback: OnItemSelectedCallback)
    : BaseAdapter<Course, FileOverviewCourseAdapter.CourseViewHolder, ItemFileCourseBinding>() {

    fun update(courseList: List<Course>) {
        items = courseList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileOverviewCourseAdapter.CourseViewHolder {
        val binding = ItemFileCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.selectedCallback = selectedCallback
        return CourseViewHolder(binding)
    }

    class CourseViewHolder(binding: ItemFileCourseBinding) : BaseViewHolder<Course, ItemFileCourseBinding>(binding) {
        override fun onItemSet() {
            binding.course = item
        }
    }
}
