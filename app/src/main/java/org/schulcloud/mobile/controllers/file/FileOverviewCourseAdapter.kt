package org.schulcloud.mobile.controllers.file

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemDirectoryCourseBinding
import org.schulcloud.mobile.models.course.Course

class FileOverviewCourseAdapter(private val onSelected: (String) -> Unit)
    : BaseAdapter<Course, FileOverviewCourseAdapter.CourseViewHolder, ItemDirectoryCourseBinding>() {

    fun update(courseList: List<Course>) {
        items = courseList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemDirectoryCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return CourseViewHolder(binding)
    }

    class CourseViewHolder(binding: ItemDirectoryCourseBinding) : BaseViewHolder<Course, ItemDirectoryCourseBinding>(binding) {
        override fun onItemSet() {
            if(item.color == null)
                item.color = "#000000"
            binding.course = item
        }
    }
}
