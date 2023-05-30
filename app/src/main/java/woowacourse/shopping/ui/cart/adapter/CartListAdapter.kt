package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.utils.PRICE_FORMAT

class CartListAdapter(
    private val onClickCloseButton: (Long) -> Unit,
    private val onClickCheckBox: (Long, Boolean) -> Unit,
    private val onClickPlus: (Long) -> Unit,
    private val onClickMinus: (Long) -> Unit
) : ListAdapter<CartItemUIState, CartListAdapter.CartListViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder.create(
            parent,
            onClickCloseButton,
            onClickCheckBox,
            onClickPlus,
            onClickMinus
        )
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setCartItems(cartItems: List<CartItemUIState>) {
        submitList(cartItems)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CartItemUIState>() {
            override fun areItemsTheSame(
                oldItem: CartItemUIState,
                newItem: CartItemUIState
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CartItemUIState,
                newItem: CartItemUIState
            ): Boolean = oldItem == newItem
        }
    }

    class CartListViewHolder private constructor(
        private val binding: ItemCartBinding,
        private val onClickCloseButton: (Long) -> Unit,
        private val onClickCheckBox: (Long, Boolean) -> Unit,
        private val onClickPlus: (Long) -> Unit,
        private val onClickMinus: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItemUIState) {
            binding.tvCartName.text = cartItem.name
            binding.tvCartPrice.text = itemView.context.getString(R.string.product_price).format(
                PRICE_FORMAT.format(cartItem.price),
            )
            Glide.with(itemView)
                .load(cartItem.imageUrl)
                .into(binding.ivCart)
            binding.cbCartItemSelected.setOnCheckedChangeListener { _, isChecked ->
                onClickCheckBox(cartItem.id, isChecked)
            }
            binding.cbCartItemSelected.isChecked = cartItem.isSelected
            binding.btnCartClose.setOnClickListener {
                onClickCloseButton(cartItem.id)
            }
            binding.counter.tvPlus.setOnClickListener { onClickPlus(cartItem.id) }
            binding.counter.tvMinus.setOnClickListener { onClickMinus(cartItem.id) }
            binding.counter.count = cartItem.count
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onClickCloseButton: (Long) -> Unit,
                onClickCheckBox: (Long, Boolean) -> Unit,
                onClickPlus: (Long) -> Unit,
                onClickMinus: (Long) -> Unit
            ): CartListViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_cart, parent, false)
                val binding = ItemCartBinding.bind(view)
                return CartListViewHolder(
                    binding,
                    onClickCloseButton,
                    onClickCheckBox,
                    onClickPlus,
                    onClickMinus
                )
            }
        }
    }
}
