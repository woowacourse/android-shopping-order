package woowacourse.shopping.view.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderDetailProductBinding
import woowacourse.shopping.model.OrderDetailProductModel

class OrderProductsAdapter(
    private val items: List<OrderDetailProductModel>,
) :
    RecyclerView.Adapter<OrderProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_product, parent, false)
        return OrderProductViewHolder(ItemOrderDetailProductBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
