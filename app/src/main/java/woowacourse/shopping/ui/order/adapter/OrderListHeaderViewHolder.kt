package woowacourse.shopping.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderListHeaderBinding
import woowacourse.shopping.ui.order.uistate.OrderUIState

class OrderListHeaderViewHolder private constructor(
    private val binding: ItemOrderListHeaderBinding,
    onClick: (Long) -> Unit
) : OrderListViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            onClick(binding?.order?.id ?: return@setOnClickListener)
        }
    }

    fun bind(order: OrderUIState) {
        binding.order = order
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClick: (Long) -> Unit
        ): OrderListHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_list_header, parent, false)
            val binding = ItemOrderListHeaderBinding.bind(view)
            return OrderListHeaderViewHolder(
                binding, onClick
            )
        }
    }
}
