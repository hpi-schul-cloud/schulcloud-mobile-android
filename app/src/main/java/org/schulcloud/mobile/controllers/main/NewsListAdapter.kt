package org.schulcloud.mobile.controllers.main

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.news.News
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

            news.createdAt?.let{
                val receivedFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val displayedFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                var displayedDate: Date? = null

                try{
                    displayedDate=receivedFormat.parse(it)
                }
                catch(e: ParseException){
                    e.printStackTrace()
                }
                holder.newsDate.text = displayedFormat.format(displayedDate)
            }

            news.content?.let{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    holder.newsContent.text  = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                }
                else{
                    @Suppress("DEPRECATION")
                    holder.newsContent.text = Html.fromHtml(it)
                }
            }
        }
    }

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val newsTitle: TextView = view.findViewById(R.id.news_title)
        val newsDate: TextView = view.findViewById(R.id.news_date)
        val newsContent: TextView = view.findViewById(R.id.news_content)
    }

}