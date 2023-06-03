package woowacourse.shopping.view.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderCartProductsModel

class OrderCartProductsAdapter(val items: List<OrderCartProductsModel.OrderCartProductModel>) :
    RecyclerView.Adapter<OrderCartProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCartProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        return OrderCartProductViewHolder(ItemOrderProductBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OrderCartProductViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
