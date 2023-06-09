package woowacourse.shopping.ui.orderdetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.OrderItemBinding
import woowacourse.shopping.model.OrderProductUIModel

class OrderDetailViewHolder(
    val binding: OrderItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderProduct: OrderProductUIModel) {
        binding.product = orderProduct.product
        binding.quantity = orderProduct.quantity
    }

    companion object {
        fun from(
            parent: ViewGroup,
        ): OrderDetailViewHolder {
            val binding = OrderItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderDetailViewHolder(binding)
        }
    }
}
