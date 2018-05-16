package org.schulcloud.mobile.controllers.main

import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.TextView
import org.schulcloud.mobile.R
import android.view.LayoutInflater
import android.support.v7.widget.RecyclerView
import org.schulcloud.mobile.models.course.Course

class CourseListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var courses: List<Course> = emptyList()

    fun update(courseList: List<Course>) {
        courses = courseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_list, parent, false)
        return CourseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val course = courses[position]
        if (holder is CourseViewHolder) {
            holder.courseTitle.text = course.name
            holder.courseTeacher.text = course.teachers?.joinToString(", "){ it.shortName() }
            holder.courseColor.setBackgroundColor(Color.parseColor(course.color))
        }
    }

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var courseTitle: TextView = view.findViewById(R.id.course_title)
        var courseTeacher: TextView = view.findViewById(R.id.course_teacher)
        var courseColor: View = view.findViewById(R.id.course_color)
    }
}