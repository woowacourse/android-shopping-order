package woowacourse.shopping.feature.orderHistory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.OrderHistoryProductUiModel

class OrderHistoryAdapter(
    products: List<OrderHistoryProductUiModel>,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<OrderHistoryViewHolder>() {

    private val products = products.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder.from(parent)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(products[position], clickListener)
    }

    fun addItems(products: List<OrderHistoryProductUiModel>) {
        this.products.addAll(products)
        notifyItemRangeInserted(itemCount, products.size)
    }
}
