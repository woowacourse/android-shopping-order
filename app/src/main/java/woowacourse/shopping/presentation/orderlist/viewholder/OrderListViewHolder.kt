package woowacourse.shopping.presentation.orderlist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class OrderListViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
) {
    private val binding = ItemOrderBinding.bind(itemView)

    fun bind(order: OrderModel) {
        binding.order = order
    }
}
