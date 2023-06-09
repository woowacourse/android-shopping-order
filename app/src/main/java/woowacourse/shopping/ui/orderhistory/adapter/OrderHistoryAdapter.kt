package woowacourse.shopping.ui.orderhistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.orderhistory.uimodel.OrderHistory

class OrderHistoryAdapter(
    private val items: List<OrderHistory>,
) : RecyclerView.Adapter<OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder(
            OrderHistoryViewHolder.getView(
                parent,
                LayoutInflater.from(parent.context),
            ),
        )
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
