package woowacourse.shopping.ui.orderHistories.historiesAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoriesBinding
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.ui.orderHistories.historiesAdapter.HistoriesListener
import woowacourse.shopping.ui.orderHistories.historyAdapter.HistoryItemAdapter

class HistoriesViewHolder(
    private val binding: ItemOrderHistoriesBinding,
    listener: HistoriesListener
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter = HistoryItemAdapter(listener::onItemClick)

    init {
        binding.rvProducts.adapter = adapter
        binding.onOrderDetailClick = listener::onOrderDetailClick
    }

    fun bind(order: OrderHistoryUIModel) {
        adapter.submitList(order.orderItems)
        binding.order = modifyFormat(order)
    }

    private fun modifyFormat(order: OrderHistoryUIModel): OrderHistoryUIModel {
        val orderDate = order.orderDate
        val newOrderDate = orderDate.substring(0, 10).replace("-", ".")
        return order.copy(orderDate = newOrderDate)
    }

    companion object {
        fun from(parent: ViewGroup, listener: HistoriesListener): HistoriesViewHolder {
            val binding = ItemOrderHistoriesBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return HistoriesViewHolder(binding, listener)
        }
    }
}
