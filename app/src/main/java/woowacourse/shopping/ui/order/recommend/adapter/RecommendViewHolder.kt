package woowacourse.shopping.ui.order.recommend.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.order.cart.viewmodel.RecommendViewModel
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel

class RecommendViewHolder(private val binding: ItemRecommendBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productViewItem: HomeViewItem.ProductViewItem,
        viewModel: RecommendViewModel,
    ) {
        binding.productViewItem = productViewItem
        binding.viewModel = viewModel
    }
}
