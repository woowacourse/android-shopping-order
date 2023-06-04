package woowacourse.shopping.ui.orderlist.orderListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.uimodel.OrderProductUIModel

class OrderProductViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false),
) {
    private val binding = ItemOrderHistoryBinding.bind(itemView)

    fun bind(orderProduct: OrderProductUIModel) {
        binding.orderItem = orderProduct
    }
}
