package woowacourse.shopping.presentation.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.CartProduct

class CartAdapter(
    private val cartActionHandler: CartActionHandler,
) : ListAdapter<CartProductUiModel, CartViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding, cartActionHandler)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CartProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CartProductUiModel,
                    newItem: CartProductUiModel,
                ): Boolean {
                    return oldItem.cartProduct.productId == newItem.cartProduct.productId
                }

                override fun areContentsTheSame(
                    oldItem: CartProductUiModel,
                    newItem: CartProductUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

class CartViewHolder(private val binding: ItemCartBinding, val cartActionHandler: CartActionHandler) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CartProductUiModel) {
        binding.cartProductUiModel = item
        binding.cartActionHandler = cartActionHandler
        binding.cbCartItem.setOnClickListener {
            cartActionHandler.onCheck(
                item,
                binding.cbCartItem.isChecked
            )
        }
    }
}
