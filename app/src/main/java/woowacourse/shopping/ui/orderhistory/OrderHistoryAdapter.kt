package woowacourse.shopping.ui.orderhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orderhistory.viewholder.OrderHistoryViewHolder

class OrderHistoryAdapter(
    private val orders: List<OrderUIModel>,
    val listener: OnHistoryOrderListener,
) : RecyclerView.Adapter<OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        return holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
