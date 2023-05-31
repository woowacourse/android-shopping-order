package woowacourse.shopping.ui.orderhistory.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder
import woowacourse.shopping.util.extension.setOnSingleClickListener

class OrderHistoryViewHolder(
    parent: ViewGroup,
    onInquiryOrderDetail: (pos: Int) -> Unit,
) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
) {
    private val binding: ItemOrderHistoryBinding = ItemOrderHistoryBinding.bind(itemView)

    init {
        binding.orderDetailButton.setOnSingleClickListener {
            onInquiryOrderDetail(absoluteAdapterPosition)
        }
    }

    override fun bind(order: ListItem) {
        binding.order = order as? UiOrder
    }
}
