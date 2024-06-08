package woowacourse.shopping.ui.cart.cartitem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.utils.ItemDiffCallback

class CartAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<CartUiModel, CartViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(
            binding,
            viewModel,
        )
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = ItemDiffCallback<CartUiModel>(
            onItemsTheSame = { old, new -> old.productId == new.productId},
            onContentsTheSame = { old, new -> old == new },
        )
    }
}
