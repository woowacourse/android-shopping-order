package woowacourse.shopping.presentation.orderlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.presentation.orderlist.viewholder.OrderListViewHolder

class OrderListAdapter(
    private val item: List<OrderModel>,
    private val onClick: (Int) -> Unit
) :
    RecyclerView.Adapter<OrderListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(parent) {
            onClick(item[it].id)
        }
    }

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(item[position])
    }
}
