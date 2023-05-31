package woowacourse.shopping.ui.shopping.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.shopping.uistate.ProductUIState

class ShoppingDiffUtil : DiffUtil.ItemCallback<ProductUIState>() {
    override fun areItemsTheSame(oldItem: ProductUIState, newItem: ProductUIState): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ProductUIState, newItem: ProductUIState): Boolean {
        return oldItem.id == newItem.id
    }
}
