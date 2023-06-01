package woowacourse.shopping.presentation.view.order.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCardListBinding
import woowacourse.shopping.presentation.model.CardModel

class CardListViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_card_list, parent, false),
) {
    private val binding = ItemCardListBinding.bind(itemView)

    fun bind(card: CardModel) {
        binding.card = card
    }
}
