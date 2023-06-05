package woowacourse.shopping.presentation.view.orderlist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderListBinding
import woowacourse.shopping.presentation.model.OrderDetailModel

class OrderListViewHolder(
    parent: ViewGroup,
    onClick: () -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_list, parent, false),
) {
    private val binding = ItemOrderListBinding.bind(itemView)

    init {
        binding.tvOrderDetail.setOnClickListener { onClick() }
    }

    fun bind(orderDetail: OrderDetailModel) {
        binding.orderDetail = orderDetail
    }
}
