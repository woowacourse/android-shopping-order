package woowacourse.shopping.ui.orderdetail.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderDetailBinding
import woowacourse.shopping.model.OrderProductModel
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.viewholder.BaseViewHolder

class OrderDetailViewHolder(parent: ViewGroup) : BaseViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
) {
    private val binding: ItemOrderDetailBinding = ItemOrderDetailBinding.bind(itemView)

    override fun bind(item: ListItem) {
        binding.orderProduct = item as? OrderProductModel
    }
}
