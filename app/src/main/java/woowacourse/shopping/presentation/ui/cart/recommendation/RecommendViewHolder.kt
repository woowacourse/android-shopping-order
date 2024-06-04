package woowacourse.shopping.presentation.ui.cart.recommendation

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class RecommendViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        shoppingProduct: ShoppingProduct,
        recommendItemCountHandler: CounterHandler,
    ) {
        binding.shoppingProduct = shoppingProduct
        binding.countHandler = recommendItemCountHandler
    }
}
