package woowacourse.shopping.ui.orderlist.orderListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoriesBinding
import woowacourse.shopping.uimodel.OrderHistoryUIModel

class OrderListViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_histories, parent, false),
) {
    private val binding = ItemOrderHistoriesBinding.bind(itemView)

    fun bind(orderHistory: OrderHistoryUIModel) {
        binding.orderHistory = orderHistory
        binding.rvOrderHistory.adapter = OrderProductAdapter(orderHistory.orderItems)
    }
}
