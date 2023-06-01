package woowacourse.shopping.ui.cart.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.ui.cart.listener.CartClickListener
import woowacourse.shopping.util.diffutil.CartDiffUtil

class CartAdapter(
    private val cartClickListener: CartClickListener,
) : ListAdapter<CartProductModel, CartViewHolder>(CartDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
        CartViewHolder(parent, cartClickListener)

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
