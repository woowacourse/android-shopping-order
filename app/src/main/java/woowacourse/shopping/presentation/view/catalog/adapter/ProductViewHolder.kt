package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class ProductViewHolder(
    private val binding: ItemProductBinding,
    eventListener: CatalogEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
        binding.categoryItemCounter.listener = eventListener
    }

    fun bind(item: CatalogItem.ProductItem) {
        binding.product = item.product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CatalogEventListener,
        ): ProductViewHolder {
            val binding =
                ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductViewHolder(binding, eventListener)
        }
    }
}
