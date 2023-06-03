package woowacourse.shopping.presentation.view.orderlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.view.orderlist.viewholder.OrderListViewHolder

class OrderListAdapter(
    private val items: List<OrderDetailModel>,
    private val onItemClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<OrderListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
