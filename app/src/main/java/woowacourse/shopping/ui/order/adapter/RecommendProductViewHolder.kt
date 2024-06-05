package woowacourse.shopping.ui.order.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.common.OnItemQuantityChangeListener

class RecommendProductViewHolder(
    private val binding: HolderProductBinding,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
        binding.onItemChargeListener = onItemQuantityChangeListener
    }
}
