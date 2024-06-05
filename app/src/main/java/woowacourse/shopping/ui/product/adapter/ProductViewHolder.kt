package woowacourse.shopping.ui.product.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.common.OnProductItemClickListener

class ProductViewHolder(
    private val binding: HolderProductBinding,
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
        binding.onProductItemClickListener = onProductItemClickListener
        binding.onItemChargeListener = onItemQuantityChangeListener
    }
}
