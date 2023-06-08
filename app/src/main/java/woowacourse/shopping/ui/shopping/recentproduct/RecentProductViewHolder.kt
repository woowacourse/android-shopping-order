package woowacourse.shopping.ui.shopping.recentproduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.support.framework.presentation.setThrottleFirstOnClickListener
import woowacourse.shopping.ui.model.UiRecentProduct

class RecentProductViewHolder(parent: ViewGroup, onItemClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_recent_product, parent, false)
    ) {
    private val binding = ItemRecentProductBinding.bind(itemView)

    init {
        binding.root.setThrottleFirstOnClickListener { onItemClick(bindingAdapterPosition) }
    }

    fun bind(recentProduct: UiRecentProduct) {
        binding.recentProduct = recentProduct
    }
}
