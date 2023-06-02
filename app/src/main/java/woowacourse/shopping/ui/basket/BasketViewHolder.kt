package woowacourse.shopping.ui.basket

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemBasketBinding
import woowacourse.shopping.ui.mapper.toBasketProductDomainModel
import woowacourse.shopping.ui.model.BasketProductUiModel
import woowacourse.shopping.ui.model.ProductUiModel

class BasketViewHolder(
    private val binding: ItemBasketBinding,
    onItemClick: (BasketProductUiModel) -> Unit,
    minusClickListener: (ProductUiModel) -> Unit,
    plusClickListener: (ProductUiModel) -> Unit,
    onCheckedChangeListener: (BasketProductUiModel, Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivCloseClickListener = onItemClick
        binding.counterBasket.plusClickListener = plusClickListener
        binding.counterBasket.minusClickListener = minusClickListener
        binding.onCheckedChangeListener = onCheckedChangeListener
    }

    fun bind(item: BasketProductUiModel) {
        binding.basketProduct = item
        binding.counterBasket.product = item.product
        binding.counterBasket.count = item.count.value
        binding.basketProductTotalPrice = item.toBasketProductDomainModel().getTotalPrice().value
    }
}
