package woowacourse.shopping.ui.orderdetail.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder
import woowacourse.shopping.ui.orderdetail.recyclerview.OrderDetailViewType
import woowacourse.shopping.ui.orderdetail.recyclerview.OrderDetailViewType.ORDER_DETAIL
import woowacourse.shopping.ui.orderdetail.recyclerview.OrderDetailViewType.PAYMENT
import woowacourse.shopping.ui.orderdetail.recyclerview.viewholder.OrderDetailViewHolder
import woowacourse.shopping.ui.orderdetail.recyclerview.viewholder.PaymentViewHolder

class OrderDetailRecyclerViewAdapter(
    private val orderDetails: MutableList<ListItem> = mutableListOf(),
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (OrderDetailViewType.of(viewType)) {
            ORDER_DETAIL -> OrderDetailViewHolder(parent)
            PAYMENT -> PaymentViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(orderDetails[position])
    }

    override fun getItemCount(): Int = orderDetails.size

    override fun getItemViewType(position: Int): Int = orderDetails[position].viewType

    fun addAll(newOrderDetails: List<ListItem>) {
        orderDetails.addAll(orderDetails)
        notifyItemRangeInserted(
            (orderDetails.size - newOrderDetails.size).coerceAtLeast(0),
            newOrderDetails.size
        )
    }
}
