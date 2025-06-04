package woowacourse.shopping.presentation.view.order.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartProductUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.view.order.cart.event.CartStateListener

class CartAdapter(
    private val eventListener: CartStateListener,
) : ListAdapter<DisplayModel<CartProductUiModel>, CartViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder = CartViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    object DiffCallBack : DiffUtil.ItemCallback<DisplayModel<CartProductUiModel>>() {
        override fun areItemsTheSame(
            oldItem: DisplayModel<CartProductUiModel>,
            newItem: DisplayModel<CartProductUiModel>,
        ): Boolean = oldItem.data.cartId == newItem.data.cartId

        override fun areContentsTheSame(
            oldItem: DisplayModel<CartProductUiModel>,
            newItem: DisplayModel<CartProductUiModel>,
        ): Boolean = oldItem == newItem
    }
}
