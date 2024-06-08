package woowacourse.shopping.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.ui.cart.listener.ShoppingCartItemListener
import woowacourse.shopping.ui.model.CartItem2

class CartAdapter(
    private val shoppingCartItemListener: ShoppingCartItemListener,
// ) : ListAdapter<CartItem, ShoppingCartItemViewHolder>(cartItemComparator) {
) : ListAdapter<CartItem2, ShoppingCartItemViewHolder>(cartItemComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingCartItemViewHolder =
        ShoppingCartItemViewHolder(
            HolderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            shoppingCartItemListener,
        )

    override fun onBindViewHolder(
        holder: ShoppingCartItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
//        private val cartItemComparator =
//            object : DiffUtil.ItemCallback<CartItem>() {
//                override fun areItemsTheSame(
//                    oldItem: CartItem,
//                    newItem: CartItem,
//                ): Boolean {
//                    return oldItem.id == newItem.id
//                }
//
//                override fun areContentsTheSame(
//                    oldItem: CartItem,
//                    newItem: CartItem,
//                ): Boolean {
//                    return oldItem == newItem
//                }
//            }

        private val cartItemComparator =
            object : DiffUtil.ItemCallback<CartItem2>() {
                override fun areItemsTheSame(
                    oldItem: CartItem2,
                    newItem: CartItem2,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CartItem2,
                    newItem: CartItem2,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
