package woowacourse.shopping.presentation.orderdetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderDetailProductBinding
import woowacourse.shopping.presentation.model.OrderDetailProductModel

class OrderDetailProductViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail_product, parent, false)
    ) {
    private val binding = ItemOrderDetailProductBinding.bind(itemView)

    fun bind(orderDetailProduct: OrderDetailProductModel) {
        binding.orderDetailProduct = orderDetailProduct
    }
}
