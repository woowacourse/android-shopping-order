package woowacourse.shopping.ui.orderhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.model.Order

class OrderHistoryAdapter(
    private val orders: List<Order>,
    private val onClicked: (order: Order) -> Unit,
) : RecyclerView.Adapter<OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {

        return OrderHistoryViewHolder.from(
            parent = parent,
            onClicked = onClicked
        )
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(orders[position])
    }
}
