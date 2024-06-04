package woowacourse.shopping.presentation.ui.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.ui.cart.CartHandler
import woowacourse.shopping.presentation.ui.model.CartModel

class CartAdapter(
    private val cartHandler: CartHandler,
    private var carts: List<CartModel> = emptyList(),
) : RecyclerView.Adapter<CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding, cartHandler)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(carts[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: List<CartModel>) {
        carts = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return carts.size
    }
}
