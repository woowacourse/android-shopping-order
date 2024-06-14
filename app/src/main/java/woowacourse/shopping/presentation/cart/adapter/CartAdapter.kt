package woowacourse.shopping.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.cart.CartActionHandler
import woowacourse.shopping.presentation.cart.model.CartUiModel

class CartAdapter(private val cartActionHandler: CartActionHandler) : ListAdapter<CartUiModel, CartViewHolder>(diffCallback) {
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
            cartActionHandler,
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
