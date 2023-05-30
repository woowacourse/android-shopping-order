package woowacourse.shopping.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.ui.model.Order

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryBinding,
    private val onClicked: (order: Order) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: Order) {
        with(binding) {
            this.order = order
            root.setOnClickListener {
                onClicked(order)
            }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClicked: (order: Order) -> Unit,
        ): OrderHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderHistoryBinding.inflate(layoutInflater, parent, false)

            return OrderHistoryViewHolder(
                binding = binding,
                onClicked = onClicked
            )
        }
    }
}
