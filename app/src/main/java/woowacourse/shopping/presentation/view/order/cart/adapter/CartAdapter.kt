package woowacourse.shopping.presentation.view.order.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

class CartAdapter(
    private val eventListener: CartEventListener,
) : ListAdapter<CartProductUiModel, CartViewHolder>(CartProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    interface CartEventListener : QuantityChangeListener {
        fun onDeleteProduct(cartId: Long)

        fun onSelectOrderProduct(productId: Long)
    }
}
