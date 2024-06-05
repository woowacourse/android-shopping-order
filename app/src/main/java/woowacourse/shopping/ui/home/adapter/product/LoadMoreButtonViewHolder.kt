package woowacourse.shopping.ui.home.adapter.product

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreButtonBinding
import woowacourse.shopping.ui.home.listener.HomeClickListener

class LoadMoreButtonViewHolder(private val binding: ItemLoadMoreButtonBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(buttonClickListener: HomeClickListener) {
        binding.clickListener = buttonClickListener
    }
}
