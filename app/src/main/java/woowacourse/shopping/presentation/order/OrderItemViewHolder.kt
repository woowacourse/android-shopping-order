package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderCartBinding
import woowacourse.shopping.presentation.model.CartProductModel

class OrderItemViewHolder(
    parent: ViewGroup,
    inflater: LayoutInflater,
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.item_order_cart, parent, false),
) {
    constructor(parent: ViewGroup) :
        this(parent, LayoutInflater.from(parent.context))

    private val binding = ItemOrderCartBinding.bind(itemView)

    fun bind(cartProductModel: CartProductModel) {
        binding.cartProductModel = cartProductModel
    }
}
