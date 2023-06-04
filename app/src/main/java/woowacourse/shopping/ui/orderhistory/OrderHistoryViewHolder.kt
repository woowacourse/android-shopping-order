package woowacourse.shopping.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.ui.model.UiOrder

class OrderHistoryViewHolder(parent: ViewGroup, onItemClick: (UiOrder) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
    ) {
    private val binding = ItemOrderBinding.bind(itemView)

    init {
        binding.onRootClickListener = onItemClick
    }

    fun bind(orderInfo: UiOrder) {
        binding.order = orderInfo
    }
}
