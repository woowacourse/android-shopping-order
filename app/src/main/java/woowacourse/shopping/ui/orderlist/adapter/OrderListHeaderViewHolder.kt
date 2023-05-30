package woowacourse.shopping.ui.orderlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderListHeaderBinding
import woowacourse.shopping.ui.orderlist.uistate.OrderUIState

class OrderListHeaderViewHolder private constructor(
    private val binding: ItemOrderListHeaderBinding
) : OrderListViewHolder(binding.root) {
    fun bind(order: OrderUIState) {
        binding.order = order
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): OrderListHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_list_header, parent, false)
            val binding = ItemOrderListHeaderBinding.bind(view)
            return OrderListHeaderViewHolder(
                binding
            )
        }
    }
}
