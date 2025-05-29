package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartRecommendBinding
import woowacourse.shopping.domain.model.Product

class CartRecommendViewHolder private constructor(
    private val binding: ItemCartRecommendBinding,
    private val onClickHandler: OnClickHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Product) {
        binding.product = item
        binding.onClickHandler = onClickHandler
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
