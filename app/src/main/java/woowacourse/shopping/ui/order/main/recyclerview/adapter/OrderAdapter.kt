package woowacourse.shopping.ui.order.main.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.UiCartProduct

class OrderAdapter(
    private val cartProducts: List<UiCartProduct>,
) : RecyclerView.Adapter<OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = cartProducts[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }
}
