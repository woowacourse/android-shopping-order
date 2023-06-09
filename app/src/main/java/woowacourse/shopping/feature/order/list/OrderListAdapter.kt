package woowacourse.shopping.feature.order.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.OrderPreviewUiModel

class OrderListAdapter(
    private val orderPreviews: List<OrderPreviewUiModel>,
    private val onClickItem: (Long) -> Unit,
) :
    RecyclerView.Adapter<OrderListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder.create(parent) { onClickItem(orderPreviews[it].orderId) }
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(orderPreviews[position])
    }

    override fun getItemCount(): Int {
        return orderPreviews.size
    }
}
