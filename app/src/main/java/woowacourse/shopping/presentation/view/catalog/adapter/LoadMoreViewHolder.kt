package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class LoadMoreViewHolder private constructor(
    binding: ItemLoadMoreBinding,
    eventListener: CatalogEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CatalogEventListener,
        ): LoadMoreViewHolder {
            val binding =
                ItemLoadMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return LoadMoreViewHolder(binding, eventListener)
        }
    }
}
