package woowacourse.shopping.feature.order.confirm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderConfirmItemBinding
import woowacourse.shopping.model.OrderProductUiModel

class OrderConfirmViewHolder(
    private val binding: ItemOrderConfirmItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderProductUiModel: OrderProductUiModel) {
        binding.orderProduct = orderProductUiModel
    }

    companion object {
        fun create(parent: ViewGroup): OrderConfirmViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderConfirmItemBinding.inflate(layoutInflater, parent, false)
            return OrderConfirmViewHolder(binding)
        }
    }
}
