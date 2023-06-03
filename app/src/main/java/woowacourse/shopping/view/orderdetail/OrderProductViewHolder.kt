package woowacourse.shopping.view.orderdetail

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.databinding.ItemOrderDetailProductBinding
import woowacourse.shopping.model.OrderDetailProductModel

class OrderProductViewHolder(
    private val binding: ItemOrderDetailProductBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: OrderDetailProductModel) {
        binding.item = item
        Glide.with(binding.root.context).load(item.imageUrl).into(binding.imgProduct)
    }
}
