package org.schulcloud.mobile.controllers.main

import android.graphics.Color
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import org.schulcloud.mobile.models.homework.Homework
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.R

class HomeworkListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var homeworkList: List<Homework> = emptyList()
    var listener: Listener? = null

    fun update(homeworkList: List<Homework>){
        this.homeworkList = homeworkList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_homework, parent, false)
        return HomeworkViewHolder(view)
    }

    override fun getItemCount(): Int {
        return homeworkList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homework = homeworkList[position]
        if (holder is HomeworkViewHolder){
            holder.homeworkTitle.text = homework.title

            homework.courseId?.let {
                holder.homeworkCourseTitle.text = it.name
                holder.homeworkCourseColor.setColorFilter(Color.parseColor(it.color))
            }

            homework.dueDate?.let{
                val dueTextAndColorId = homework.getDueTextAndColorId()
                holder.homeworkDueTill.text = dueTextAndColorId.first
                holder.homeworkDueTill.setTextColor(dueTextAndColorId.second)

                if(dueTextAndColorId.second == Color.RED){
                    holder.homeworkDueTillFlag.visibility = View.VISIBLE
                }
                else{
                    holder.homeworkDueTillFlag.visibility = View.GONE
                }
            }

            homework.description?.let{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    holder.homeworkDescription.text  = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                }
                else{
                    @Suppress("DEPRECATION")
                    holder.homeworkDescription.text = Html.fromHtml(it)
                }

                holder.homeworkDescription.text = holder.homeworkDescription.text.toString().trim()
            }

            if(headerRequired(position)){
                holder.homeworkHeaderLayout.visibility = View.VISIBLE
                holder.homeworkHeader.text = getHeaderText(position)
            }
            else{
                holder.homeworkHeaderLayout.visibility = View.GONE
            }

            holder.homeworkClickable.setOnClickListener({
                v -> listener?.onClick(homework.id)
            })
        }
    }

    private fun headerRequired(position: Int): Boolean{
        if (position == 0 ||  homeworkList[position].getDueTimespanDays() != homeworkList[position-1].getDueTimespanDays())
            return true
        return false
    }

    private fun getHeaderText(position: Int): String{
        when (homeworkList[position].getDueTimespanDays()){
            -1 -> {
                return "Gestern"
            }
            0 -> {
                return "Heute"
            }
            1 -> {
                return "Morgen"
            }
            Int.MIN_VALUE -> {
                return ""
            }
            else -> {
                var dateText = ""
                try {
                    dateText = DateTimeFormat.forPattern("dd.MM.yyyy").print(homeworkList[position].getDueTillDateTime())
                } catch (e: Exception){
                }
                return dateText
            }
        }
    }

    interface Listener{
        fun onClick(id: String)
    }

    class HomeworkViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val homeworkTitle: TextView = view.findViewById(R.id.homework_title)
        val homeworkDueTill: TextView = view.findViewById(R.id.homework_duetill)
        val homeworkDueTillFlag: ImageView = view.findViewById(R.id.homework_duetill_flag)
        val homeworkDescription: TextView = view.findViewById(R.id.homework_description)
        val homeworkCourseTitle: TextView = view.findViewById(R.id.homework_course_title)
        val homeworkCourseColor: ImageView = view.findViewById(R.id.homework_course_color)
        val homeworkHeader: TextView = view.findViewById(R.id.homework_header)
        val homeworkHeaderLayout: LinearLayout = view.findViewById(R.id.homework_header_layout)
        val homeworkClickable: LinearLayout = view.findViewById(R.id.homework_clickable_layout)
    }
}