package org.schulcloud.mobile.controllers.topic

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseViewHolder.Companion.createBinding
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic

/**
 * Date: 6/10/2018
 */
class ContentListAdapter : RecyclerView.Adapter<ContentViewHolder<out ViewDataBinding>>() {
    companion object {
        private val CONTENT_TYPES = arrayOf(ContentWrapper.COMPONENT_TEXT,
                ContentWrapper.COMPONENT_RESOURCES, ContentWrapper.COMPONENT_GEOGEBRA,
                ContentWrapper.COMPONENT_ETHERPAD, ContentWrapper.COMPONENT_NEXBOARD)
    }

    private lateinit var topic: Topic
    private var contents: List<ContentWrapper> = emptyList()

    fun update(topic: Topic?) {
        if (topic != null)
            this.topic = topic
        this.contents = topic?.contents ?: emptyList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder<out ViewDataBinding> {
        return when (viewType) {
            0 -> TextViewHolder(createBinding(parent, R.layout.item_content_text))
            1 -> ResourcesViewHolder(createBinding(parent, R.layout.item_content_resources))
            3 -> EtherpadViewHolder(createBinding(parent, R.layout.item_content_etherpad))
            else -> UnsupportedViewHolder(createBinding(parent, R.layout.item_content_unsupported))
        }
    }

    override fun getItemCount(): Int = contents.size

    override fun getItemViewType(position: Int): Int {
        val component = contents[position].component
        return CONTENT_TYPES.indexOfFirst { it.equals(component, true) }
    }

    override fun onBindViewHolder(holder: ContentViewHolder<out ViewDataBinding>, position: Int) {
        holder.topic = topic
        holder.item = contents[position]
    }
}
