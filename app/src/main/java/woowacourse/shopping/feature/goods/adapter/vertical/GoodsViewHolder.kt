package woowacourse.shopping.feature.goods.adapter.vertical

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemGoodsBinding
import woowacourse.shopping.domain.model.CartItem

class GoodsViewHolder(
    val binding: ItemGoodsBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
    }
}
