package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.CartProductState

class OrderProductViewHolder(
    private val binding: ItemOrderProductBinding
) : ViewHolder(binding.root) {

    fun bind(orderProduct: CartProductState) {
        binding.orderProduct = orderProduct
    }

    companion object {
        fun getView(parent: ViewGroup): ItemOrderProductBinding {
            val inflater = LayoutInflater.from(parent.context)
            return ItemOrderProductBinding.inflate(inflater, parent, false)
        }
    }
}
