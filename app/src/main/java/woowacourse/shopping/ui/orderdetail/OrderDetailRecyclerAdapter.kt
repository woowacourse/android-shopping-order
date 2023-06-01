package woowacourse.shopping.ui.orderdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.model.OrderUiModel

class OrderDetailRecyclerAdapter(
    private val order: OrderUiModel,
) : RecyclerView.Adapter<OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {

        return OrderDetailViewHolder.from(parent)
    }

    override fun getItemCount(): Int = order.uiOrderProducts.size

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(order.uiOrderProducts[position])
    }
}
