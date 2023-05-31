package woowacourse.shopping.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.ui.model.Order

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryBinding,
    private val onClicked: (order: Order) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: Order) {
        with(binding) {
            tvOrderCount.text = root.context.getString(
                R.string.tv_order_count,
                order.orderProducts.size
            )
            tvOrderDate.text = root.context.getString(
                R.string.tv_order_date,
                order.orderDate
            )
            tvOrderProducts.text = root.context.getString(
                R.string.tv_order_products,
                order.orderProducts.first().productName,
                order.orderProducts.size - COUNT_DIFF
            )
            root.setOnClickListener {
                onClicked(order)
            }
        }
    }

    companion object {
        private const val COUNT_DIFF = 1

        fun from(
            parent: ViewGroup,
            onClicked: (order: Order) -> Unit,
        ): OrderHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderHistoryBinding.inflate(layoutInflater, parent, false)

            return OrderHistoryViewHolder(
                binding = binding,
                onClicked = onClicked
            )
        }
    }
}
