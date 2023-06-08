package woowacourse.shopping.ui.orderhistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.OrderHistoryItemBinding
import woowacourse.shopping.ui.orderhistory.uimodel.OrderHistory

class OrderHistoryViewHolder(
    private val binding: OrderHistoryItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderHistory: OrderHistory) {
        binding.orderInfo = orderHistory
    }

    companion object {
        fun getView(parent: ViewGroup, layoutInflater: LayoutInflater): OrderHistoryItemBinding =
            OrderHistoryItemBinding.inflate(layoutInflater, parent, false)
    }
}
