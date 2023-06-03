package woowacourse.shopping.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemIndividualOrderBinding
import woowacourse.shopping.ui.model.OrderItem

class OrderDetailViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate((R.layout.item_individual_order), parent, false)
) {
    private val binding = ItemIndividualOrderBinding.bind(itemView)

    fun bind(orderItem: OrderItem) {
        binding.orderItem = orderItem
    }
}
