package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemOrderPendingCartBinding
import woowacourse.shopping.model.CartProductState

class OrderPendingCartViewHolder(
    private val binding: ItemOrderPendingCartBinding
) : ViewHolder(binding.root) {

    fun bind(orderPendingCart: CartProductState) {
        binding.orderPendingCart = orderPendingCart
    }

    companion object {
        fun getView(parent: ViewGroup): ItemOrderPendingCartBinding {
            val inflater = LayoutInflater.from(parent.context)
            return ItemOrderPendingCartBinding.inflate(inflater, parent, false)
        }
    }
}
