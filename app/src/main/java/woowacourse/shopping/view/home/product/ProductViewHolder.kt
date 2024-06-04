package woowacourse.shopping.view.home.product

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.home.HomeEventListener
import woowacourse.shopping.view.home.HomeQuantityEventListener

class ProductViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productItem: HomeViewItem.ProductViewItem,
        homeClickListener: HomeEventListener,
        quantityClickListener: QuantityEventListener,
    ) {
        binding.productItem = productItem
        binding.shoppingClickListener = homeClickListener
        binding.quantityClickListener = quantityClickListener
    }
}
