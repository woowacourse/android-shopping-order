package woowacourse.shopping.feature.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartProductBinding
import woowacourse.shopping.model.CartProductState

class CartProductViewHolder(
    binding: ViewDataBinding,
    private val onCartProductDeleteClick: (CartProductState) -> Unit,
    private val minusQuantity: (cartProductState: CartProductState) -> Unit,
    private val plusQuantity: (cartProductState: CartProductState) -> Unit,
    private val updateChecked: (cartId: Long, checked: Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val binding = binding as ItemCartProductBinding

    fun bind(cartProductState: CartProductState) {
        binding.cartProduct = cartProductState
        binding.counterView.count = cartProductState.quantity

        binding.cartProductCheckBox.isChecked = cartProductState.isPicked
        binding.cartClearImageView.setOnClickListener {
            updateChecked(cartProductState.id, false)
            onCartProductDeleteClick(cartProductState)
        }
        binding.counterView.plusClickListener = {
            plusQuantity(cartProductState)
            binding.counterView.count = cartProductState.quantity
        }
        binding.counterView.minusClickListener = {
            minusQuantity(cartProductState)
            binding.counterView.count = cartProductState.quantity
        }
        binding.cartProductCheckBox.setOnClickListener {
            updateChecked(cartProductState.id, binding.cartProductCheckBox.isChecked)
        }
    }

    companion object {
        fun createInstance(
            parent: ViewGroup,
            onCartProductDeleteClick: (CartProductState) -> Unit,
            minusQuantity: (cartProductState: CartProductState) -> Unit,
            plusQuantity: (cartProductState: CartProductState) -> Unit,
            updateChecked: (productId: Long, checked: Boolean) -> Unit
        ): CartProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCartProductBinding.inflate(inflater, parent, false)
            return CartProductViewHolder(
                binding, onCartProductDeleteClick, minusQuantity, plusQuantity, updateChecked
            )
        }
    }
}
