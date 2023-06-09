package woowacourse.shopping.presentation.view.order.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.CardModel

class CardDiffUtil(
    private val oldItems: List<CardModel>,
    private val newItems: List<CardModel>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}
