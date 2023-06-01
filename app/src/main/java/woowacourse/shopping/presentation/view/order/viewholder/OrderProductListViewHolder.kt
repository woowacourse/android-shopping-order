package woowacourse.shopping.presentation.view.order.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductListBinding
import woowacourse.shopping.presentation.model.CartModel

class OrderProductListViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_order_product_list, parent, false),
) {
    private val binding = ItemOrderProductListBinding.bind(itemView)

    fun bind(cart: CartModel) {
        binding.cart = cart
    }
}
