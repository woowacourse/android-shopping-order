package woowacourse.shopping.ui.order.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.OrderItemBinding
import woowacourse.shopping.model.CartProductUIModel

class OrderViewHolder private constructor(
    val binding: OrderItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cart: CartProductUIModel) {
        binding.product = cart.product
        binding.quantity = cart.quantity
    }

    companion object {
        fun from(
            parent: ViewGroup,
        ): OrderViewHolder {
            val binding = OrderItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderViewHolder(binding)
        }
    }
}
