package woowacourse.shopping.view.home.product

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.view.home.HomeEventListener

class LoadMoreButtonViewHolder(private val binding: ItemLoadMoreButtonBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(buttonClickListener: HomeEventListener) {
        binding.clickListener = buttonClickListener
    }
}
