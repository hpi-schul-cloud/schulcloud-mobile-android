package org.schulcloud.mobile.controllers.main

import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.course.Course

class CourseListAdapter(private val selectedCallback: (id: String) -> Unit) : RecyclerView.Adapter<CourseListAdapter.CourseViewHolder>() {

    private var courses: List<Course> = emptyList()

    fun update(courseList: List<Course>) {
        courses = courseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListAdapter.CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_list, parent, false)
        return CourseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onBindViewHolder(holder: CourseListAdapter.CourseViewHolder, position: Int) {
        courses[position].apply {
            holder.card.setOnClickListener { selectedCallback(id) }
            holder.courseTitle.text = name
            holder.courseTeacher.text = teachers?.joinToString(", ") { it.shortName() }
            holder.courseColor.setBackgroundColor(Color.parseColor(color))
        }
    }


    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.card)
        var courseTitle: TextView = view.findViewById(R.id.course_title)
        var courseTeacher: TextView = view.findViewById(R.id.course_teacher)
        var courseColor: View = view.findViewById(R.id.course_color)
    }
}
