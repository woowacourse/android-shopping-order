package woowacourse.shopping.ui.orderhistory.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.OrderHistoryItemBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orderhistory.OnHistoryOrderListener

class OrderHistoryViewHolder private constructor(
    val binding: OrderHistoryItemBinding,
    val listener: OnHistoryOrderListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderUIModel) {
        binding.order = order
        binding.listener = listener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: OnHistoryOrderListener,
        ): OrderHistoryViewHolder {
            val binding = OrderHistoryItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderHistoryViewHolder(binding, listener)
        }
    }
}
