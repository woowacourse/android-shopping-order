package woowacourse.shopping.feature.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderDetailProductBinding
import woowacourse.shopping.model.OrderProductUiModel

class OrderDetailProductViewHolder(
    private val binding: ItemOrderDetailProductBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: OrderProductUiModel) {
        binding.orderProduct = item
    }

    companion object {
        fun create(parent: ViewGroup): OrderDetailProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderDetailProductBinding.inflate(layoutInflater, parent, false)
            return OrderDetailProductViewHolder(binding)
        }
    }
}
