package woowacourse.shopping.ui.orderfinish

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.model.OrderRecord

class OrderDetailRecyclerAdapter(
    private val orderRecord: OrderRecord,
) : RecyclerView.Adapter<OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {

        return OrderDetailViewHolder.from(parent)
    }

    override fun getItemCount(): Int = orderRecord.orderProducts.size

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(orderRecord.orderProducts[position])
    }
}
