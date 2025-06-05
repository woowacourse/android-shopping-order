package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Cart

class CartAdapter(
    private val cartClickListener: CartViewHolder.CartClickListener,
) : ListAdapter<Cart, CartViewHolder>(CartDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, cartClickListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        val item: Cart = getItem(position)
        holder.bind(item)
    }
}
