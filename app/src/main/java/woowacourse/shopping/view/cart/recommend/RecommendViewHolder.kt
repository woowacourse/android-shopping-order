package woowacourse.shopping.view.cart.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemRecommentBinding
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.state.ProductState

class RecommendViewHolder(
    private val binding: ItemRecommentBinding,
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
                LayoutInflater.from(parent.context).inflate(R.layout.item_recomment, parent, false)
            val binding = ItemRecommentBinding.bind(inflater)
            return RecommendViewHolder(binding, handler, quantityHandler)
        }
    }

    interface Handler {
        fun showQuantity(productId: Long)
    }
}
