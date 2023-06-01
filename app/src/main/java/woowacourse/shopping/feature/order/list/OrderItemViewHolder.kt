package woowacourse.shopping.feature.order.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderMinInfoBinding
import woowacourse.shopping.model.OrderMinInfoItemUiModel

class OrderItemViewHolder(
    private val binding: ItemOrderMinInfoBinding,
    orderItemClickListener: OrderItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.listener = orderItemClickListener
    }

    fun bind(orderMinInfoItemUiModel: OrderMinInfoItemUiModel) {
        binding.orderItem = orderMinInfoItemUiModel
    }

    companion object {
        fun create(
            parent: ViewGroup,
            orderItemClickListener: OrderItemClickListener
        ): OrderItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderMinInfoBinding.inflate(layoutInflater, parent, false)
            return OrderItemViewHolder(binding, orderItemClickListener)
        }
    }
}
