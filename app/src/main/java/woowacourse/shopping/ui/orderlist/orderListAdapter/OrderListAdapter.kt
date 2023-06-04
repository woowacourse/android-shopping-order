package woowacourse.shopping.ui.orderlist.orderListAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.uimodel.OrderHistoryUIModel

class OrderListAdapter(
    private val orderHistories: List<OrderHistoryUIModel>,
) : RecyclerView.Adapter<OrderListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(parent)
    }

    override fun getItemCount(): Int = orderHistories.size

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(orderHistories[position])
    }
}
