package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.view.catalog.CatalogEventHandler
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class ProductViewHolder private constructor(
    private val binding: ItemProductBinding,
    catalogEventHandler: CatalogEventHandler,
    counterEventHandler: ItemCounterEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventHandler = catalogEventHandler
        binding.categoryItemCounter.eventHandler = counterEventHandler
    }

    fun bind(item: CatalogItem.ProductItem) {
        binding.product = item.product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            catalogEventHandler: CatalogEventHandler,
            counterEventHandler: ItemCounterEventHandler,
        ): ProductViewHolder {
            val binding =
                ItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return ProductViewHolder(binding, catalogEventHandler, counterEventHandler)
        }
    }
}
