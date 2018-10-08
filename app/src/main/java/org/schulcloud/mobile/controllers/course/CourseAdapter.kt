package org.schulcloud.mobile.controllers.course

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemCourseBinding
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.utils.setForegroundForJellyBean

class CourseAdapter(private val onSelected: (String) -> Unit) :
        BaseAdapter<Course, CourseAdapter.CourseViewHolder, ItemCourseBinding>() {

    fun update(courseList: List<Course>) {
        items = courseList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        binding.materialCard.setForegroundForJellyBean(binding.materialCard.context)
        return CourseViewHolder(binding)
    }

    class CourseViewHolder(binding: ItemCourseBinding) : BaseViewHolder<Course, ItemCourseBinding>(binding) {
        companion object {
            @JvmStatic
            fun teachersToShort(teachers: List<User>?): String {
                return teachers?.joinToString(", ") { it.shortName } ?: ""
            }
        }

        override fun onItemSet() {
            binding.course = item
        }
    }
}
