package woowacourse.shopping.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.CartItemBinding
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.product.catalog.QuantityControlListener

class CartViewHolder(
    private val binding: CartItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartProduct: ProductUiModel) {
        binding.cartProduct = cartProduct
        binding.checkboxSelection.isChecked = cartProduct.isChecked
    }

    companion object {
        fun from(
            parent: ViewGroup,
            cartActionListener: CartActionListener,
            quantityControlListener: QuantityControlListener,
        ): CartViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CartItemBinding.inflate(inflater, parent, false)
            binding.cartActionListener = cartActionListener
            binding.layoutQuantityControlBar.quantityControlListener = quantityControlListener

            return CartViewHolder(binding)
        }
    }
}
