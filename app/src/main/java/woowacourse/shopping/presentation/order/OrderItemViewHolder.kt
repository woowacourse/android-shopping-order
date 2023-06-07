package woowacourse.shopping.presentation.order

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderProductModel

class OrderItemViewHolder(
    private val binding: ItemOrderBinding,
    private val updateProductPrice: (TextView, OrderProductModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderProductModel: OrderProductModel) {
        binding.orderProduct = orderProductModel
        updateProductPrice(binding.textOrderProductPrice, orderProductModel)
    }
}
