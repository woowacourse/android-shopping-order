package woowacourse.shopping.ui.order.orderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.CartProductUIModel

class OrderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false),
) {
    private val binding = ItemOrderBinding.bind(itemView)

    fun bind(orderItem: CartProductUIModel) {
        binding.orderItem = orderItem
    }
}
