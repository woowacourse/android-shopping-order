package woowacourse.shopping.feature.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartState

class OrderPendingCartListAdapter(
    private val orderProducts: CartState
) : RecyclerView.Adapter<OrderPendingCartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderPendingCartViewHolder {
        return OrderPendingCartViewHolder(OrderPendingCartViewHolder.getView(parent))
    }

    override fun getItemCount(): Int = orderProducts.products.size

    override fun onBindViewHolder(holder: OrderPendingCartViewHolder, position: Int) {
        holder.bind(orderProducts.products[position])
    }
}
