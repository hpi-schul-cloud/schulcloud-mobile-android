package org.schulcloud.mobile.controllers.topic

import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.realm.Realm
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.*
import org.schulcloud.mobile.models.content.ContentRepository
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.utils.HEADER_REFERER
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl


/**
 * Date: 6/11/2018
 */
sealed class ContentViewHolder<B : ViewDataBinding>(binding: B) : BaseViewHolder<ContentWrapper, B>(binding) {
    lateinit var topic: Topic
}

class TextViewHolder(binding: ItemContentTextBinding) : ContentViewHolder<ItemContentTextBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
    }
}

class ResourcesViewHolder(binding: ItemContentResourcesBinding) : ContentViewHolder<ItemContentResourcesBinding>(binding) {
    private val adapter: ResourceListAdapter

    init {
        adapter = ResourceListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ResourcesViewHolder.adapter
        }
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content

        adapter.update(item.content?.resources ?: emptyList())
    }

    fun openExternal() {
        openUrl(context, item.content?.url.asUri(), mapOf(HEADER_REFERER to topic.url))
    }
}

class GeogebraViewHolder(binding: ItemContentGeogebraBinding) : ContentViewHolder<ItemContentGeogebraBinding>(binding) {
    companion object {
        private const val GEOGEBRA = "https://www.geogebra.org/m/"
    }

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this

        binding.preview.setImageResource(0)
        item.content?.materialId?.also {
            ContentRepository.geogebraPreviewUrl(realm, it).observe(this, Observer {
                Glide.with(context).load(it?.previewUrl.asUri()).into(binding.preview)
            })
        }
    }

    fun openExternal() {
        openUrl(context, (GEOGEBRA + item.content?.materialId).asUri())
    }
}

class EtherpadViewHolder(binding: ItemContentEtherpadBinding) : ContentViewHolder<ItemContentEtherpadBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this

        binding.contentView.referer = "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"
        binding.contentView.loadUrl(item.content?.url, mutableMapOf(HEADER_REFERER to "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"))
    }

    fun openExternal() {
        openUrl(context, item.content?.url.asUri(), mapOf(HEADER_REFERER to topic.url))
    }
}

class NexboardViewHolder(binding: ItemContentNexboardBinding) : ContentViewHolder<ItemContentNexboardBinding>(binding) {
    companion object {
        private const val URL_SUFFIX = "?username=Test&stickypad=false"
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this

        binding.contentView.loadUrl(item.content?.url + URL_SUFFIX)
    }

    fun openExternal() {
        openUrl(context, (item.content?.url + URL_SUFFIX).asUri())
    }
}

class UnsupportedViewHolder(binding: ItemContentUnsupportedBinding) : ContentViewHolder<ItemContentUnsupportedBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
    }
}
