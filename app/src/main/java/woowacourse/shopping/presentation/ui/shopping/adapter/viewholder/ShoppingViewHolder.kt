package woowacourse.shopping.presentation.ui.shopping.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.ui.counter.CounterHandler
import woowacourse.shopping.presentation.ui.shopping.ShoppingEventHandler

class ShoppingViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        shoppingProduct: ShoppingProduct,
        eventHandler: ShoppingEventHandler,
        shoppingItemCountHandler: CounterHandler,
    ) {
        binding.shoppingProduct = shoppingProduct
        binding.eventHandler = eventHandler
        binding.countHandler = shoppingItemCountHandler
    }
}
