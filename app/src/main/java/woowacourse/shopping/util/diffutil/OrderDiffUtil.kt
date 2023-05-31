package woowacourse.shopping.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.model.UiOrder

object OrderDiffUtil : DiffUtil.ItemCallback<UiOrder>() {
    override fun areItemsTheSame(oldItem: UiOrder, newItem: UiOrder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UiOrder, newItem: UiOrder): Boolean {
        return oldItem == newItem
    }
}
