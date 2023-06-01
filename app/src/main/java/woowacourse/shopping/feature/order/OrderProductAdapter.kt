package woowacourse.shopping.feature.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.CartProductUiModel

class OrderProductAdapter(
    private val products: List<CartProductUiModel>
) : RecyclerView.Adapter<OrderProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder.from(parent)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(products[position])
    }
}
