package woowacourse.shopping.view.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProdcutBinding
import woowacourse.shopping.model.uimodel.OrderProductUIModel

class OrderDetailViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_order_prodcut, parent, false)
) {

    private val binding = ItemOrderProdcutBinding.bind(itemView)
    private lateinit var orderProduct: OrderProductUIModel

    fun bind(item: OrderProductUIModel) {
        orderProduct = item
        binding.orderProduct = orderProduct
    }
}
