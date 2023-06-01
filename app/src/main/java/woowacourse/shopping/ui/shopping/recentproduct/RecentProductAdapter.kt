package woowacourse.shopping.ui.shopping.recentproduct

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.model.RecentProductUiModel

class RecentProductAdapter(private val onItemClick: (ProductUiModel) -> Unit) :
    ListAdapter<RecentProductUiModel, RecentProductViewHolder>(recentProductDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentProductViewHolder =
        RecentProductViewHolder(parent) { onItemClick(currentList[it].product) }

    override fun onBindViewHolder(holder: RecentProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val recentProductDiffUtil = object : DiffUtil.ItemCallback<RecentProductUiModel>() {
            override fun areItemsTheSame(oldItem: RecentProductUiModel, newItem: RecentProductUiModel):
                Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RecentProductUiModel, newItem: RecentProductUiModel):
                Boolean = oldItem == newItem
        }
    }
}
