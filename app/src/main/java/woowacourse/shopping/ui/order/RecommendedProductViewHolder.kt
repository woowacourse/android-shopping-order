package woowacourse.shopping.ui.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.OnItemQuantityChangeListener

class RecommendedProductViewHolder(
    private val binding: HolderProductBinding,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
        binding.onItemChargeListener = onItemQuantityChangeListener
    }
}
