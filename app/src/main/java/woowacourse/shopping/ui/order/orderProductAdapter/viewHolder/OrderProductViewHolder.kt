package woowacourse.shopping.ui.order.orderProductAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.CartProductUIModel

class OrderProductViewHolder(
    private val binding: ItemOrderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: CartProductUIModel) {
        binding.order = product
    }

    companion object {
        fun from(parent: ViewGroup): OrderProductViewHolder {
            val binding = ItemOrderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderProductViewHolder(binding)
        }
    }
}
