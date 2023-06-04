package woowacourse.shopping.presentation.orderlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class OrderItemViewHolder(
    parent: ViewGroup,
    inflater: LayoutInflater,
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.item_order, parent, false),
) {
    constructor(parent: ViewGroup) :
        this(parent, LayoutInflater.from(parent.context))

    private val binding = ItemOrderBinding.bind(itemView)

    fun bind(orderModel: OrderModel) {
        binding.orderModel = orderModel
    }
}
