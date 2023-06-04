package woowacourse.shopping.presentation.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderDetailBinding
import woowacourse.shopping.presentation.model.OrderProductModel

class OrderProductItemViewHolder(
    parent: ViewGroup,
    inflater: LayoutInflater,
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.item_order_detail, parent, false),
) {
    constructor(parent: ViewGroup) :
        this(parent, LayoutInflater.from(parent.context))

    private val binding = ItemOrderDetailBinding.bind(itemView)

    fun bind(orderProductModel: OrderProductModel) {
        binding.orderProductModel = orderProductModel
    }
}
