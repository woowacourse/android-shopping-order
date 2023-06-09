package woowacourse.shopping.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryListBinding
import woowacourse.shopping.ui.model.OrderHistoryModel

class OrderHistoryAdapter(
    private val histories: List<OrderHistoryModel>,
    private val onShowDetailListener: (Int) -> Unit
): RecyclerView.Adapter<OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder(
            ItemOrderHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onShowDetailListener
        )
    }

    override fun getItemCount(): Int = histories.size

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(histories[position])
    }
}