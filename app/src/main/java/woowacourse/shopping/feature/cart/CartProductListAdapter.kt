package woowacourse.shopping.feature.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartProductState

class CartProductListAdapter(
    private var cartProductStates: List<CartProductState> = listOf(),
    private val onCartProductDeleteClick: (cartProductState: CartProductState) -> Unit,
    private val minusQuantity: (cartProductState: CartProductState) -> Unit,
    private val plusQuantity: (cartProductState: CartProductState) -> Unit,
    private val updateChecked: (productId: Long, checked: Boolean) -> Unit
) : RecyclerView.Adapter<CartProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder.createInstance(
            parent, onCartProductDeleteClick, minusQuantity, plusQuantity, updateChecked
        )
    }

    override fun getItemCount(): Int {
        return cartProductStates.size
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        holder.bind(cartProductStates[position])
    }

    fun setItems(cartProducts: List<CartProductState>) {
        this.cartProductStates = cartProducts.toList()
        notifyDataSetChanged()
    }

    fun updateItem(cartProductState: CartProductState) {
        val index = cartProductStates.indexOfFirst { it.id == cartProductState.id }
        if (index != -1) notifyItemChanged(index)
    }
}
