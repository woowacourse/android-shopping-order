package woowacourse.shopping.ui.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductListBinding
import woowacourse.shopping.ui.model.CartProductModel

class OrderProductViewHolder(
    private val binding: ItemOrderProductListBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: CartProductModel) {
        binding.cartProduct = product
    }
}