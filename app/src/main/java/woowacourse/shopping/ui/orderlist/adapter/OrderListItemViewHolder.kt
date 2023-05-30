package woowacourse.shopping.ui.orderlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderListBinding
import woowacourse.shopping.ui.productlist.uistate.ProductUIState

class OrderListItemViewHolder private constructor(
    private val binding: ItemOrderListBinding
) : OrderListViewHolder(binding.root) {
    fun bind(product: ProductUIState) {
        binding.product = product
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): OrderListItemViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_order_list, parent, false)
            val binding = ItemOrderListBinding.bind(view)
            return OrderListItemViewHolder(
                binding
            )
        }
    }
}
