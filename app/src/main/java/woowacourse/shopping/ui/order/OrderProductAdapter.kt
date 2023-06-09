package woowacourse.shopping.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductListBinding
import woowacourse.shopping.ui.model.CartProductModel

class OrderProductAdapter(
    private val products: List<CartProductModel>
) : RecyclerView.Adapter<OrderProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(
            ItemOrderProductListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(products[position])
    }
}