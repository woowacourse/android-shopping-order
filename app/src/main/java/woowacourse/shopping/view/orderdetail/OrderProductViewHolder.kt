package woowacourse.shopping.view.orderdetail

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.domain.model.ProductWithQuantity

class OrderProductViewHolder(
    private val binding: ItemOrderProductBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ProductWithQuantity) {
        binding.textProductName.text = item.product.name
        binding.textProductPrice.text =
            binding.root.context.getString(R.string.korean_won, item.product.price.price)
        binding.textProductQuantity.text =
            binding.root.context.getString(R.string.product_quantity, item.quantity)
        Glide.with(binding.root.context).load(item.product.imageUrl).into(binding.imgProduct)
    }
}
