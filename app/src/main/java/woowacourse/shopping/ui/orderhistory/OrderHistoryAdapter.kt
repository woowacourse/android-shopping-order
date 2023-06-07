package woowacourse.shopping.ui.orderhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.model.OrderUiModel

class OrderHistoryAdapter(
    private val orders: List<OrderUiModel>,
    private val onClicked: (order: OrderUiModel) -> Unit,
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
