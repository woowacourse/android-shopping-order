package woowacourse.shopping.ui.order.adapter.recommend

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel

class RecommendViewHolder(private val binding: ItemRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productViewItem: HomeViewItem.ProductViewItem,
        viewModel: OrderViewModel,
    ) {
        binding.productViewItem = productViewItem
        binding.viewModel = viewModel
    }
}
