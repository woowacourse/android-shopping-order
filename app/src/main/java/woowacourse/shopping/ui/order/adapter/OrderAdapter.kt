package woowacourse.shopping.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartProductUIModel

class OrderAdapter(
    private val items: List<CartProductUIModel>,
) : RecyclerView.Adapter<OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(OrderViewHolder.getView(parent, LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
