package woowacourse.shopping.ui.shopping.recyclerview.adapter.recentproduct

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.RecentProductModel
import woowacourse.shopping.util.diffutil.RecentProductDiffUtil

class RecentProductAdapter(private val onItemClick: (RecentProductModel) -> Unit) :
    ListAdapter<RecentProductModel, RecentProductViewHolder>(RecentProductDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentProductViewHolder =
        RecentProductViewHolder(parent) { pos -> onItemClick(getItem(pos)) }

    override fun onBindViewHolder(holder: RecentProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
