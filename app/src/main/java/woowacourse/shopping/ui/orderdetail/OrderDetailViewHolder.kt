package woowacourse.shopping.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderItemBinding
import woowacourse.shopping.ui.model.OrderItem

class OrderDetailViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate((R.layout.item_order_item), parent, false)
) {
    private val binding = ItemOrderItemBinding.bind(itemView)

    fun bind(orderItem: OrderItem) {
        binding.orderItem = orderItem
    }
}
