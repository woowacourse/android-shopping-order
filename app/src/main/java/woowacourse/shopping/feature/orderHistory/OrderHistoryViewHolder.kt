package woowacourse.shopping.feature.orderHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.OrderHistoryProductUiModel

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        orderProduct: OrderHistoryProductUiModel,
        onClickListener: (Int) -> Unit
    ) {
        binding.orderProduct = orderProduct
        binding.tvNavigateDetail.setOnClickListener {
            onClickListener(orderProduct.orderId)
        }
    }

    companion object {

        fun from(parent: ViewGroup): OrderHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderHistoryBinding.inflate(layoutInflater, parent, false)

            return OrderHistoryViewHolder(binding)
        }
    }
}
