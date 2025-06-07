package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartRecommendBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.custom.CartCountView

class CartRecommendViewHolder private constructor(
    private val binding: ItemCartRecommendBinding,
    private val onClickHandler: OnClickHandler,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var item: Product

    init {
        binding.onClickHandler = onClickHandler
    }

    fun bind(item: Product) {
        this.item = item
        binding.product = item
        binding.cartRecommendProductCount.setOnClickHandler(
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
        fun onIncreaseClick(productId: Long)

        fun onDecreaseClick(productId: Long)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClickHandler: OnClickHandler,
        ): CartRecommendViewHolder {
            val inflate =
                ItemCartRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CartRecommendViewHolder(inflate, onClickHandler)
        }
    }
}
