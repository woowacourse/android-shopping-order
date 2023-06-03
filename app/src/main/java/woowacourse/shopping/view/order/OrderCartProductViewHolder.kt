package woowacourse.shopping.view.order

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderCartProductsModel

class OrderCartProductViewHolder(private val binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: OrderCartProductsModel.OrderCartProductModel) {
        binding.item = item
        Glide.with(binding.root.context).load(item.imageUrl).into(binding.imgProduct)
    }
}
