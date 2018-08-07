package org.schulcloud.mobile.controllers.news

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemNewsBinding
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.utils.parseDate

class NewsAdapter
    : BaseAdapter<News, NewsAdapter.NewsViewHolder, ItemNewsBinding>() {

    fun update(newsList: List<News>) {
        items = newsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    class NewsViewHolder(binding: ItemNewsBinding) : BaseViewHolder<News, ItemNewsBinding>(binding) {
        override fun onItemSet() {
            binding.news = item
            binding.formattedDate = item.createdAt?.parseDate()?.let {
                DateUtils.formatDateTime(context, it.timeInMillis, DateUtils.FORMAT_SHOW_DATE)
            } ?: ""
        }
    }
}
