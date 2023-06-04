package woowacourse.shopping.feature.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartState

class OrderProductListAdapter(
    private val orderProducts: CartState
) : RecyclerView.Adapter<OrderProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(OrderProductViewHolder.getView(parent))
    }

    override fun getItemCount(): Int = orderProducts.products.size

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(orderProducts.products[position])
    }
}
