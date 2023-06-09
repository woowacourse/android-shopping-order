package woowacourse.shopping.view.orderhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.uimodel.OrderUIModel

class OrderHistoryAdapter(
    private var orderHistories: List<OrderUIModel>,
    private val clickListener: OrderHistoryClickListener
) : RecyclerView.Adapter<OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder(parent, clickListener)
    }

    override fun getItemCount(): Int = orderHistories.size

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(orderHistories[position])
    }

    fun update(orders: List<OrderUIModel>) {
        orderHistories = orders
        notifyDataSetChanged()
    }
}
