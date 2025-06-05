package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.custom.CartCountView

class CartProductViewHolder(
    private val binding: ItemCartBinding,
    private val onClickHandler: OnClickHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClickHandler = onClickHandler
    }

    fun bind(item: Product) {
        binding.product = item
        binding.cartProductCount.setOnClickHandler(
            object : CartCountView.OnClickHandler {
                override fun onIncreaseClick() {
                    onClickHandler.onIncreaseClick(item.productDetail.id)
                }

                override fun onDecreaseClick() {
                    onClickHandler.onDecreaseClick(item.productDetail.id)
                }
            },
        )
    }

    interface OnClickHandler {
        fun onRemoveCartProductClick(
            cartId: Long,
            productId: Long,
        )

        fun onIncreaseClick(productId: Long)

        fun onDecreaseClick(productId: Long)

        fun onSelectClick(cartId: Long)
    }
}
