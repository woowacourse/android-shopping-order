package woowacourse.shopping.presentation.ui.shopping.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.ui.shopping.ShoppingActionHandler

sealed class ShoppingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val shoppingActionHandler: ShoppingActionHandler,
    ) :
        ShoppingViewHolder(binding.root) {
        fun bind(item: CartProduct) {
            binding.cartProduct = item
            binding.shoppingActionHandler = shoppingActionHandler
        }
    }

    class LoadViewHolder(
        private val binding: ItemLoadBinding,
        private val shoppingActionHandler: ShoppingActionHandler,
    ) : ShoppingViewHolder(binding.root) {
        fun bind() {
            binding.shoppingActionHandler = shoppingActionHandler
        }
    }
}
