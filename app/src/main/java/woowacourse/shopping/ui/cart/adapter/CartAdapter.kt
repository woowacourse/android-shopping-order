package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.CartListener
import woowacourse.shopping.ui.cart.CartUiModel

class CartAdapter(private val cartListener: CartListener) : ListAdapter<CartUiModel, CartViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(
            getItem(position),
            cartListener,
        )
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<CartUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CartUiModel,
                    newItem: CartUiModel,
                ): Boolean {
                    return oldItem.cartItemId == newItem.cartItemId
                }

                override fun areContentsTheSame(
                    oldItem: CartUiModel,
                    newItem: CartUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
