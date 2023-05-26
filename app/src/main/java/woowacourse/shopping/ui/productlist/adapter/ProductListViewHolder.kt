package woowacourse.shopping.ui.productlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.productlist.ProductListEvent
import woowacourse.shopping.ui.productlist.uistate.ProductUIState

class ProductListViewHolder private constructor(
    private val binding: ItemProductBinding,
    productListEvent: ProductListEvent
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.productListEvent = productListEvent
        binding.counter.binding.counterEvent = productListEvent
    }

    fun bind(product: ProductUIState) {
        binding.product = product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            productListEvent: ProductListEvent
        ): ProductListViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            val binding = ItemProductBinding.bind(view)
            return ProductListViewHolder(
                binding, productListEvent
            )
        }
    }
}
