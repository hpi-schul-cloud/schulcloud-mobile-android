package org.schulcloud.mobile.controllers.main

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import org.joda.time.format.DateTimeFormat
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
        if (position == 0 || items[position].getDueTimespanDays() != items[position - 1].getDueTimespanDays())
            return true
        return false
    }

    class HomeworkViewHolder(binding: ItemHomeworkBinding) : BaseViewHolder<Homework, ItemHomeworkBinding>(binding) {
        companion object {
            @JvmStatic
            fun getHeaderText(homework: Homework): String {
                when (homework.getDueTimespanDays()) {
                    -1 -> {
                        return "Gestern"
                    }
                    0 -> {
                        return "Heute"
                    }
                    1 -> {
                        return "Morgen"
                    }
                    Int.MAX_VALUE -> {
                        return ""
                    }
                    else -> {
                        return try {
                            DateTimeFormat.forPattern("dd.MM.yyyy").print(homework.getDueTillDateTime())
                        } catch (e: IllegalArgumentException) {
                            ""
                        }
                    }
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

            @JvmStatic
            fun dueLabelFlagRequired(homework: Homework): Boolean {
                return (homework.getDueTimespanDays() <= 1)
            }
        }

        override fun onItemSet() {
            binding.homework = item
        }
    }
}
