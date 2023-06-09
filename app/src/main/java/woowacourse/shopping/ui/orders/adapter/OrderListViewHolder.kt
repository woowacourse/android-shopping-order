package woowacourse.shopping.ui.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderListBinding
import woowacourse.shopping.ui.orders.uistate.OrdersItemUIState
import woowacourse.shopping.utils.PRICE_FORMAT

class OrderListViewHolder private constructor(
    private val binding: ItemOrderListBinding,
    private val onClickOrder: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClickOrder(binding.order?.id ?: return@setOnClickListener)
        }
    }

    fun bind(order: OrdersItemUIState) {
        binding.order = order
        binding.tvOrderNumber.text = order.id.toString()
        binding.tvOrderPrice.text = itemView.context.getString(R.string.format_price)
            .format(PRICE_FORMAT.format(order.totalPrice))
        binding.tvOrderDescription.text = order.orderDescription
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickOrder: (Long) -> Unit
        ): OrderListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_list, parent, false)
            val binding = ItemOrderListBinding.bind(view)
            return OrderListViewHolder(binding, onClickOrder)
        }
    }
}
