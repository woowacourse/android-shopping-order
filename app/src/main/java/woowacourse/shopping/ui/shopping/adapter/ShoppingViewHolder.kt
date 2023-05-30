package woowacourse.shopping.ui.shopping.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.shopping.ShoppingEvent
import woowacourse.shopping.ui.shopping.uistate.ProductUIState

class ShoppingViewHolder private constructor(
    private val binding: ItemProductBinding,
    shoppingEvent: ShoppingEvent
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.productListEvent = shoppingEvent
        binding.counter.binding.counterEvent = shoppingEvent
    }

    fun bind(product: ProductUIState) {
        binding.product = product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            shoppingEvent: ShoppingEvent
        ): ShoppingViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            val binding = ItemProductBinding.bind(view)
            return ShoppingViewHolder(
                binding, shoppingEvent
            )
        }
    }
}
