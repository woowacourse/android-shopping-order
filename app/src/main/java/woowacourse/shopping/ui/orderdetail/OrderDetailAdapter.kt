package woowacourse.shopping.ui.orderdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.OrderProductUIModel
import woowacourse.shopping.ui.orderdetail.viewholder.OrderDetailViewHolder

class OrderDetailAdapter(
    private val cartProducts: List<OrderProductUIModel>,
) : RecyclerView.Adapter<OrderDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        return OrderDetailViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        return holder.bind(cartProducts[position])
    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }
}
