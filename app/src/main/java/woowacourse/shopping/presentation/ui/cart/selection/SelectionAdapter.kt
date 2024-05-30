package woowacourse.shopping.presentation.ui.cart.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem

class SelectionAdapter(
    private val selectionEventHandler: SelectionEventHandler,
    private val selectionCountHandler: SelectionCountHandler,
) : RecyclerView.Adapter<SelectionViewHolder>() {
    private var cartItems: List<CartItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SelectionViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SelectionViewHolder,
        position: Int,
    ) {
        val cartItem = cartItems[position]
        return holder.bind(cartItem, selectionEventHandler, selectionCountHandler)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun loadData(cartItems: List<CartItem>) {
        this.cartItems = cartItems
        notifyDataSetChanged()
    }
}
