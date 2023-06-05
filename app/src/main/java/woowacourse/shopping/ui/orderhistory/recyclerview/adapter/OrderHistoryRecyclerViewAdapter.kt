package woowacourse.shopping.ui.orderhistory.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder
import woowacourse.shopping.ui.orderhistory.recyclerview.viewholder.OrderHistoryViewHolder
import woowacourse.shopping.util.diffutil.OrderDiffUtil

class OrderHistoryRecyclerViewAdapter(
    private val onInquiryOrderDetail: (order: OrderModel) -> Unit,
) : ListAdapter<OrderModel, BaseViewHolder>(OrderDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OrderHistoryViewHolder(parent) { pos -> onInquiryOrderDetail(getItem(pos)) }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
