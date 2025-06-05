package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.presentation.view.catalog.CatalogEventHandler

class LoadMoreViewHolder private constructor(
    binding: ItemLoadMoreBinding,
    catalogEventHandler: CatalogEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventHandler = catalogEventHandler
    }

    companion object {
        fun from(
            parent: ViewGroup,
            catalogEventHandler: CatalogEventHandler,
        ): LoadMoreViewHolder {
            val binding =
                ItemLoadMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return LoadMoreViewHolder(binding, catalogEventHandler)
        }
    }
}
