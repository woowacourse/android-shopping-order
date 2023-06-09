package woowacourse.shopping.ui.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.ui.order.viewholder.OrderViewHolder

class OrderAdapter(
    private val cartItems: List<CartProductUIModel>,
) : RecyclerView.Adapter<OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        return holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
