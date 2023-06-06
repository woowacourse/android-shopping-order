package woowacourse.shopping.presentation.order.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.presentation.model.OrderCartModel

class OrderCartViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
    ) {
    private val binding = ItemOrderProductBinding.bind(itemView)

    fun bind(cartInfo: OrderCartModel) {
        binding.orderCart = cartInfo
    }
}
