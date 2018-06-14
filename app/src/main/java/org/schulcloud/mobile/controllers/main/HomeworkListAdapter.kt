package org.schulcloud.mobile.controllers.main

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import org.schulcloud.mobile.models.homework.Homework
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.schulcloud.mobile.R

class HomeworkListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var homeworkList: List<Homework> = emptyList()

    fun update(homeworkList: List<Homework>){
        this.homeworkList = homeworkList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_homework_list, parent, false)
        return HomeworkViewHolder(view)
    }

    override fun getItemCount(): Int {
        return homeworkList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // TODO: formatting
        val homework = homeworkList[position]
        if (holder is HomeworkViewHolder){
            holder.homeworkTitle.text = homework.title
            holder.homeworkDueTill.text = homework.dueDate
            holder.homeworkDescription.text = homework.description

            homework.courseId?.let {
                holder.homeworkCourseTitle.text = it.name
                holder.homeworkCourseColor.setColorFilter(Color.parseColor(it.color))
            }

        }
    }

    class HomeworkViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val homeworkTitle: TextView = view.findViewById(R.id.homework_title)
        val homeworkDueTill: TextView = view.findViewById(R.id.homework_dueTill)
        val homeworkDescription: TextView = view.findViewById(R.id.homework_description)
        val homeworkCourseTitle: TextView = view.findViewById(R.id.homework_course_title)
        val homeworkCourseColor: ImageView = view.findViewById(R.id.homework_course_color)
    }
}