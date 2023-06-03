package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderProduct

class OrderProductViewHolder(
    private val binding: ItemOrderProductBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: OrderProduct) {
        binding.orderProduct = product
    }

    companion object {

        fun from(parent: ViewGroup): OrderProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderProductBinding.inflate(layoutInflater, parent, false)

            return OrderProductViewHolder(binding)
        }
    }
}
