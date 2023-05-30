package woowacourse.shopping.ui.orderfinish

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.model.Order

class OrderDetailRecyclerAdapter(
    private val order: Order,
) : RecyclerView.Adapter<OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {

        return OrderDetailViewHolder.from(parent)
    }

    override fun getItemCount(): Int = order.orderProducts.size

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(order.orderProducts[position])
    }
}
