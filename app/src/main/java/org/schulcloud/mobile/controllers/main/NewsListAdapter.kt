package org.schulcloud.mobile.controllers.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_news_list.view.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.news.News

class NewsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var newsList: List <News> = emptyList()

    fun update (newsList: List<News>){
        this.newsList=newsList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_list, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val news: News = newsList[position]
        if (holder is NewsViewHolder){
            holder.newsTitle.text = news.title
            holder.newsDate.text = news.createdAt
            if (news.content!= null) {
                holder.newsContent.text = news.content!!
            }
        }
    }

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var newsTitle: TextView = view.findViewById(R.id.news_title)
        var newsDate: TextView = view.findViewById(R.id.news_date)
        var newsContent: TextView = view.findViewById(R.id.news_content)
    }

}