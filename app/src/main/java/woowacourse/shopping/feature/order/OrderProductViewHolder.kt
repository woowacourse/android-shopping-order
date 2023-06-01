package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.CartProductUiModel

class OrderProductViewHolder(private val binding: ItemOrderProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(product: CartProductUiModel) {
        binding.cartProduct = product
    }

    companion object {
        fun create(parent: ViewGroup): OrderProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderProductBinding.inflate(inflater, parent, false)
            return OrderProductViewHolder(binding)
        }
    }
}
