package woowacourse.shopping.ui.order.main.recyclerview.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.UiCartProduct

class OrderViewHolder(
    private val binding: ItemOrderProductBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartProduct: UiCartProduct) {
        binding.cartProduct = cartProduct
    }
}
