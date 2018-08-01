package org.schulcloud.mobile.controllers.topic

import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.realm.Realm
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.*
import org.schulcloud.mobile.models.content.ContentRepository
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic
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
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
            }
            adapter = this@ResourcesViewHolder.adapter
        }
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content

        adapter.update(item.content?.resources ?: emptyList())
    }

    fun openExternal() {
        context.openUrl(item.content?.url.asUri())
    }
}

class InternalViewHolder(binding: ItemContentInternalBinding) : ContentViewHolder<ItemContentInternalBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this
    }

    fun openExternal() {
        context.openUrl(item.content?.url.asUri())
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
            ContentRepository.geogebraPreviewUrl(realm, it).observe(context as BaseActivity, Observer {
                Glide.with(context as BaseActivity).load(it?.previewUrl.asUri()).into(binding.preview)
            })
        }
    }

    fun openExternal() {
        context.openUrl((GEOGEBRA + item.content?.materialId).asUri())
    }
}

class EtherpadViewHolder(binding: ItemContentEtherpadBinding) : ContentViewHolder<ItemContentEtherpadBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this
    }

    fun openExternal() {
        context.openUrl(topic.url.asUri())
    }
}

class NexboardViewHolder(binding: ItemContentNexboardBinding) : ContentViewHolder<ItemContentNexboardBinding>(binding) {
    companion object {
        private const val URL_SUFFIX = "?username=Test"
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this
    }

    fun openExternal() {
        context.openUrl((item.content?.url + URL_SUFFIX).asUri())
    }
}

class UnsupportedViewHolder(binding: ItemContentUnsupportedBinding) : ContentViewHolder<ItemContentUnsupportedBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
    }
}
