package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel

class RecommendProductAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<ProductWithQuantityUiModel, RecommendProductViewHolder>(RecommendProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder {
        val itemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RecommendProductViewHolder(itemProductBinding, viewModel)
    }

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
