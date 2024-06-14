package woowacourse.shopping.ui.product.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.common.OnProductItemClickListener
import woowacourse.shopping.databinding.HolderProductHistoryBinding
import woowacourse.shopping.domain.model.Product

class ProductHistoryViewHolder(
    private val binding: HolderProductHistoryBinding,
    private val onProductItemClickListener: OnProductItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
        binding.onProductItemClickListener = onProductItemClickListener
    }
}
