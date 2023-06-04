package woowacourse.shopping.ui.order.detail.recyclerview.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderDetailBinding
import woowacourse.shopping.model.UiOrderedProduct

class DetailViewHolder(
    private val binding: ItemOrderDetailBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(orderedProduct: UiOrderedProduct) {
        binding.orderedProduct = orderedProduct
    }
}
