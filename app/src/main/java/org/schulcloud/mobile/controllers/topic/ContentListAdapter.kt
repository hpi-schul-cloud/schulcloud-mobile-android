package org.schulcloud.mobile.controllers.topic

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder.Companion.createBinding
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic

/**
 * Date: 6/10/2018
 */
class ContentListAdapter
    : BaseAdapter<ContentWrapper, ContentViewHolder<out ViewDataBinding>, ViewDataBinding>() {
    companion object {
        private val CONTENT_TYPES = arrayOf(ContentWrapper.COMPONENT_TEXT,
                ContentWrapper.COMPONENT_RESOURCES, ContentWrapper.COMPONENT_INTERNAL,
                ContentWrapper.COMPONENT_GEOGEBRA, ContentWrapper.COMPONENT_ETHERPAD,
                ContentWrapper.COMPONENT_NEXBOARD)
    }

    private lateinit var topic: Topic

    fun update(topic: Topic?) {
        if (topic != null)
            this.topic = topic
        items = topic?.contents?.filter { it.hidden != true } ?: emptyList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder<out ViewDataBinding> {
        return when (viewType) {
            0 -> TextViewHolder(createBinding(parent, R.layout.item_content_text))
            1 -> ResourcesViewHolder(createBinding(parent, R.layout.item_content_resources))
            2 -> InternalViewHolder(createBinding(parent, R.layout.item_content_internal))
            3 -> GeogebraViewHolder(createBinding(parent, R.layout.item_content_geogebra))
            4 -> EtherpadViewHolder(createBinding(parent, R.layout.item_content_etherpad))
            5 -> NexboardViewHolder(createBinding(parent, R.layout.item_content_nexboard))
            else -> UnsupportedViewHolder(createBinding(parent, R.layout.item_content_unsupported))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val component = items[position].component
        return CONTENT_TYPES.indexOfFirst { it.equals(component, true) }
    }

    override fun onBindViewHolder(holder: ContentViewHolder<out ViewDataBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.topic = topic
    }
}
