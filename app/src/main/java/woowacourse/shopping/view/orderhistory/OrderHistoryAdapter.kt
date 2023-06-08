package woowacourse.shopping.view.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderDetailModel

class OrderHistoryAdapter(
    private val items: List<OrderDetailModel>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<OrderHistoryViewHolder>() {

    fun interface OnItemClick {
        fun onItemClick(orderId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderHistoryViewHolder(ItemOrderBinding.bind(view), onItemClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
