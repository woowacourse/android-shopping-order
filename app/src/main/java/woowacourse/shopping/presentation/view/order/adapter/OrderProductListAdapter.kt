package woowacourse.shopping.presentation.view.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.order.viewholder.OrderProductListViewHolder

class OrderProductListAdapter(
    private val items: List<CartModel>,
) : RecyclerView.Adapter<OrderProductListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductListViewHolder {
        return OrderProductListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: OrderProductListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
