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
    private val updateChecked: (productId: Long, checked: Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val binding = binding as ItemCartProductBinding

    fun bind(cartProductState: CartProductState) {
        binding.cartProduct = cartProductState
        binding.counterView.count = cartProductState.quantity

        binding.cartProductCheckBox.isChecked = cartProductState.isPicked
        binding.cartClearImageView.setOnClickListener {
            updateChecked(cartProductState.productId, false)
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
            updateChecked(cartProductState.productId, binding.cartProductCheckBox.isChecked)
        }

        /*
            홀더 또한 뷰이기 때문에 값을 더하거나 빼는 등의 로직은 presenter와 같은 위치에 있는 것이 좋을 것 같으나
            어떻게 하면 좋을지 잘 모르겠습니다...
            이런 로직을 holder 밖으로 뺄 수 있는 방법이 있을까요?
         */
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
