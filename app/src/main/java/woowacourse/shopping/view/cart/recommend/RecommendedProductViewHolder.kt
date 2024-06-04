package woowacourse.shopping.view.cart.recommend

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.cart.recommend.RecommendProductEventListener
import woowacourse.shopping.view.home.product.HomeViewItem

class RecommendedProductViewHolder(private val binding: ItemRecommendProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        productItem: HomeViewItem.ProductViewItem,
        productClickListener: RecommendProductEventListener,
        quantityClickListener: QuantityEventListener,
    ) {
        binding.productItem = productItem
        binding.shoppingClickListener = productClickListener
        binding.quantityClickListener = quantityClickListener
    }
}
