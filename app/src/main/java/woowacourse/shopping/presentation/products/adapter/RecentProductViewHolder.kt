package woowacourse.shopping.presentation.products.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.presentation.products.uimodel.RecentProductUiModel

class RecentProductViewHolder(private val binding: ItemRecentProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(recentProductUiModel: RecentProductUiModel) {
        binding.recentProduct = recentProductUiModel
    }
}
