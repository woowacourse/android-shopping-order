package woowacourse.shopping.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.OrderItemBinding
import woowacourse.shopping.model.CartProductUIModel

class OrderViewHolder(
    private val binding: OrderItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productUIModel: CartProductUIModel) {
        binding.product = productUIModel
    }

    companion object {
        fun getView(parent: ViewGroup, layoutInflater: LayoutInflater): OrderItemBinding =
            OrderItemBinding.inflate(layoutInflater, parent, false)
    }
}
