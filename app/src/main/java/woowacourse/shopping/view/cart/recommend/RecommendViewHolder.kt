package woowacourse.shopping.view.cart.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.state.ProductState

class RecommendViewHolder(
    private val binding: ItemRecommendBinding,
    private val handler: Handler,
    private val quantityHandler: CartQuantityHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ProductState) {
        with(binding) {
            model = item
            adapterEventHandler = handler
            cartQuantityEventHandler = quantityHandler
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            handler: Handler,
            quantityHandler: CartQuantityHandler,
        ): RecommendViewHolder {
            val inflater =
                LayoutInflater.from(parent.context).inflate(R.layout.item_recommend, parent, false)
            val binding = ItemRecommendBinding.bind(inflater)
            return RecommendViewHolder(binding, handler, quantityHandler)
        }
    }

    interface Handler {
        fun showQuantity(productId: Long)
    }
}
