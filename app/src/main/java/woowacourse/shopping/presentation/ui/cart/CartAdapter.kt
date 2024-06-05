package woowacourse.shopping.presentation.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.ui.cart.selection.CartItemSelectionEventHandler

class CartAdapter(
    private val cartEventHandler: CartItemSelectionEventHandler,
    private val cartItemCountHandler: CartItemCountHandler,
) : RecyclerView.Adapter<CartViewHolder>() {
    private var cartItems: List<CartItemUiModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        val cartItem = cartItems[position]
        return holder.bind(cartItem, cartEventHandler, cartItemCountHandler)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun loadData(cartItems: List<CartItemUiModel>) {
        this.cartItems = cartItems
        notifyDataSetChanged()
    }
}
