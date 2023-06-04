package woowacourse.shopping.view.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.uimodel.OrderUIModel

class OrderHistoryViewHolder(
    parent: ViewGroup,
    private val clickListener: OrderHistoryClickListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
) {
    private val binding = ItemOrderHistoryBinding.bind(itemView)
    private lateinit var order: OrderUIModel

    init {
        binding.loOrder.setOnClickListener {
            clickListener.orderItemOnClick(order)
        }
    }

    fun bind(item: OrderUIModel) {
        order = item
        binding.order = order
        if (order.totalAmount == 1) {
            binding.tvOrderDetailSummary.text = order.previewName
        }
    }
}
