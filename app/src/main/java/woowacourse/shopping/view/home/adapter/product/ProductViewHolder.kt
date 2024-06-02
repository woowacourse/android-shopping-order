package woowacourse.shopping.view.home.adapter.product

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.view.home.viewmodel.HomeViewModel

class ProductViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productItem: HomeViewItem.ProductViewItem,
        viewModel: HomeViewModel,
    ) {
        binding.productItem = productItem
        binding.viewModel = viewModel
    }
}
