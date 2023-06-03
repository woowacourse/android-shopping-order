package woowacourse.shopping.feature.orderDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderProductUiModel

class OrderDetailAdapter(
    private val items: List<OrderProductUiModel>
) : RecyclerView.Adapter<OrderProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val binding = DataBindingUtil.inflate<ItemOrderProductBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_order_product,
            parent,
            false
        )
        return OrderProductViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
