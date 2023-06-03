package woowacourse.shopping.view.order

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderCartProductsModel

class OrderCartProductViewHolder(private val binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: OrderCartProductsModel.OrderCartProductModel) {
        binding.textProductName.text = item.name
        binding.textProductPrice.text = binding.root.context.getString(R.string.korean_won, item.price)
        binding.textProductQuantity.text = binding.root.context.getString(R.string.product_quantity, item.quantity)
        Glide.with(binding.root.context).load(item.imageUrl).into(binding.imgProduct)
    }
}
