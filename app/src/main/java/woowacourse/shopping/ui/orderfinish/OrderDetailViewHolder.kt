package woowacourse.shopping.ui.orderfinish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.ui.model.OrderProduct

class OrderDetailViewHolder(
    private val binding: ItemOrderProductBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderProduct: OrderProduct) {
        binding.orderedProduct = orderProduct
    }

    companion object {
        fun from(parent: ViewGroup): OrderDetailViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderProductBinding.inflate(layoutInflater)

            return OrderDetailViewHolder(binding)
        }
    }
}
