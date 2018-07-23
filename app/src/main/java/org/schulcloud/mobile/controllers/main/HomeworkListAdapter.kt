package org.schulcloud.mobile.controllers.main

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemHomeworkBinding
import org.schulcloud.mobile.models.homework.Homework

class HomeworkListAdapter(private val onSelected: (String) -> Unit) : BaseAdapter<Homework, HomeworkListAdapter.HomeworkViewHolder, ItemHomeworkBinding>() {

    fun update(homeworkList: List<Homework>) {
        items = homeworkList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkListAdapter.HomeworkViewHolder {
        val binding = ItemHomeworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return HomeworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeworkListAdapter.HomeworkViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.headerRequired = headerRequired(position)
    }

    private fun headerRequired(position: Int): Boolean {
        if (position == 0 || items[position].dueTimespanDays != items[position - 1].dueTimespanDays)
            return true
        return false
    }

    class HomeworkViewHolder(binding: ItemHomeworkBinding) : BaseViewHolder<Homework, ItemHomeworkBinding>(binding) {
        companion object {
            @JvmStatic
            fun getHeaderText(homework: Homework): String {
                return when (homework.dueTimespanDays) {
                    -1 -> SchulCloudApp.instance.resources.getString(R.string.yesterday)
                    0 -> SchulCloudApp.instance.resources.getString(R.string.today)
                    1 -> SchulCloudApp.instance.resources.getString(R.string.tomorrow)
                    Int.MAX_VALUE -> SchulCloudApp.instance.resources.getString(R.string.homework_error_invalidDueDate)
                    else -> DateTimeFormat.mediumDate().print(homework.dueDateTime)
                }
            }

            @JvmStatic
            fun getTextFromHtml(text: String?): String {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Html.fromHtml(text ?: "", Html.FROM_HTML_MODE_LEGACY).toString().trim()
                } else {
                    @Suppress("DEPRECATION")
                    return Html.fromHtml(text ?: "").toString().trim()
                }
            }
        }

        override fun onItemSet() {
            binding.homework = item
        }
    }
}
