package woowacourse.shopping.presentation.orderdetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.OrderDetailProductModel
import woowacourse.shopping.presentation.orderdetail.viewholder.OrderDetailProductViewHolder

class OrderDetailProductAdapter(private val orderProducts: List<OrderDetailProductModel>) :
    RecyclerView.Adapter<OrderDetailProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailProductViewHolder {
        return OrderDetailProductViewHolder(parent)
    }

    override fun getItemCount(): Int = orderProducts.size

    override fun onBindViewHolder(holder: OrderDetailProductViewHolder, position: Int) {
        holder.bind(orderProducts[position])
    }
}
