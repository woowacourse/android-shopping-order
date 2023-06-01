package woowacourse.shopping.presentation.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.common.OrderCartDiffItemCallback
import woowacourse.shopping.presentation.model.OrderCartInfoModel
import woowacourse.shopping.presentation.order.viewholder.OrderCartViewHolder

class OrderCartAdapter() :
    ListAdapter<OrderCartInfoModel, OrderCartViewHolder>(OrderCartDiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCartViewHolder {
        return OrderCartViewHolder(parent)
    }

    override fun onBindViewHolder(holder: OrderCartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
