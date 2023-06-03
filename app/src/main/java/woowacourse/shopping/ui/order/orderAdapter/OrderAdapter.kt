package woowacourse.shopping.ui.order.orderAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartProductUIModel

class OrderAdapter(
    private val orderItems: List<CartProductUIModel>,
) : RecyclerView.Adapter<OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(parent)
    }

    override fun getItemCount(): Int = orderItems.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderItems[position])
    }
}
