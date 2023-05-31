package woowacourse.shopping.ui.order.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderProduct
import woowacourse.shopping.ui.order.recyclerview.ListItem

class OrderViewHolder(
    parent: ViewGroup,
) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
) {
    private val binding: ItemOrderBinding = ItemOrderBinding.bind(itemView)

    override fun bind(item: ListItem) {
        binding.orderProduct = item as? OrderProduct
    }
}
