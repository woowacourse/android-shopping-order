package woowacourse.shopping.view.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.domain.model.ProductWithQuantity

class OrderProductsAdapter(
    private val items: List<ProductWithQuantity>,
) :
    RecyclerView.Adapter<OrderProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        return OrderProductViewHolder(ItemOrderProductBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
