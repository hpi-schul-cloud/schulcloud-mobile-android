package org.schulcloud.mobile.controllers.main

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemNewsBinding
import org.schulcloud.mobile.models.news.News
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsListAdapter : BaseAdapter<News, NewsListAdapter.NewsViewHolder, ItemNewsBinding>() {

    fun update(newsList: List<News>) {
        items = newsList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsListAdapter.NewsViewHolder {
        val binding = ItemNewsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    class NewsViewHolder(binding: ItemNewsBinding) :
            BaseViewHolder<News, ItemNewsBinding>(binding) {
        companion object {
            @JvmStatic
            fun getShortDate(longDateString: String): String {
                val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val displayedFormat = SimpleDateFormat("dd.MM.yyyy")
                val displayedDate: Date?

                try {
                    displayedDate = receivedFormat.parse(longDateString)
                } catch (e: ParseException) {
                    return ""
                }
                return displayedFormat.format(displayedDate)
            }
        }

        override fun onItemSet() {
            binding.news = item
        }
    }
}
