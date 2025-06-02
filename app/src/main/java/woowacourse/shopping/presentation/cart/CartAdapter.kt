package woowacourse.shopping.presentation.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.CartItemUiModel

class CartAdapter(
    private val cartCounterClickListener: CartCounterClickListener,
    private val cartPageClickListener: CartPageClickListener,
) : ListAdapter<CartItemUiModel, CartViewHolder>(CartDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.create(parent, cartCounterClickListener, cartPageClickListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
