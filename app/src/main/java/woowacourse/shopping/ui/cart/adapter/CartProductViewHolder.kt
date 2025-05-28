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

    fun bind(product: Product) {
        binding.product = product
        binding.cartProductCount.setOnClickHandler(
            object : CartCountView.OnClickHandler {
                override fun onIncreaseClick() {
                    onClickHandler.onIncreaseClick(product.productDetail.id)
                }

                override fun onDecreaseClick() {
                    onClickHandler.onDecreaseClick(product.productDetail.id)
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
    }
}
