package woowacourse.shopping.ui.home.adapter.product

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.home.viewmodel.HomeViewModel

class ProductViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productViewItem: HomeViewItem.ProductViewItem,
        viewModel: HomeViewModel,
    ) {
        binding.productViewItem = productViewItem
        binding.viewModel = viewModel
    }
}
