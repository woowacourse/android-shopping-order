package woowacourse.shopping.presentation.view.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.view.order.viewholder.CardListViewHolder

class CardListAdapter(
    items: List<CardModel>
) : RecyclerView.Adapter<CardListViewHolder>() {
    private val items = items.toMutableList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListViewHolder {
        return CardListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CardListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].id

    fun updateList(newItems: List<CardModel>) {
        val diffUtilCallback = CardDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}
