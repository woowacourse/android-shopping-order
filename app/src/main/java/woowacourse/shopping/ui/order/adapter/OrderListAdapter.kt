package woowacourse.shopping.ui.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.ui.order.uistate.OrderUIState

class OrderListAdapter(private val orders: MutableList<OrderUIState>) :
    RecyclerView.Adapter<OrderListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return when (viewType) {
            R.layout.item_order_list_header -> OrderListHeaderViewHolder.from(parent)
            else -> OrderListItemViewHolder.from(parent)
        }
    }

    override fun getItemCount(): Int = orders.sumOf { it.cart.size + 1 }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        var sum = 0
        orders.forEachIndexed { index, item ->
            val offset = sum + 1
            if (position == sum) {
                (holder as OrderListHeaderViewHolder).bind(orders[index])
                return
            }
            sum += item.cart.size + 1
            if (position < sum) {
                (holder as OrderListItemViewHolder).bind(orders[index].cart[position - offset])
                return
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var sum = 0
        orders.forEach {
            if (position == sum) return R.layout.item_order_list_header
            sum += it.cart.size + 1
            if (position < sum) return R.layout.item_order_list
        }
        return R.layout.item_order_list
    }

    fun setOrders(orders: List<OrderUIState>) {
        this.orders.apply {
            clear()
            addAll(orders)
        }
        notifyItemRangeInserted(0, itemCount)
    }
}
