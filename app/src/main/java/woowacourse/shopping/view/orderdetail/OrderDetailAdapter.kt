package woowacourse.shopping.view.orderdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.uimodel.OrderProductUIModel

class OrderDetailAdapter(
    private var orderProducts: List<OrderProductUIModel>
) : RecyclerView.Adapter<OrderDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder(parent)
    }

    override fun getItemCount(): Int = orderProducts.size

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(orderProducts[position])
    }

    fun update(orderItems: List<OrderProductUIModel>) {
        orderProducts = orderItems
    }
}
