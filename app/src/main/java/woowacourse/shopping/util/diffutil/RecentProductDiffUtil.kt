package woowacourse.shopping.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.model.RecentProductModel

object RecentProductDiffUtil : DiffUtil.ItemCallback<RecentProductModel>() {
    override fun areItemsTheSame(
        oldItem: RecentProductModel,
        newItem: RecentProductModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: RecentProductModel,
        newItem: RecentProductModel,
    ): Boolean = oldItem == newItem
}
