package woowacourse.shopping.view.shoppingCartRecommend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.view.common.ProductQuantityClickListener
import woowacourse.shopping.view.product.ProductsItem

class RecommendProductViewHolder(
    private val binding: ItemRecommendProductBinding,
    productListener: ProductQuantityClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.productClickListener = productListener
    }

    fun bind(item: ProductsItem.ProductItem) {
        binding.productItem = item
        if (item.selectedQuantity == 0) {
            handleEmptySelectedQuantityVisibility()
            return
        }
        handleSelectedQuantityVisibility(item.selectedQuantity)
    }

    private fun handleEmptySelectedQuantityVisibility() {
        binding.productQuantityComponentLayout.visibility = View.GONE
        binding.productPlusQuantityButtonDefault.visibility = View.VISIBLE
    }

    private fun handleSelectedQuantityVisibility(quantity: Int) {
        binding.productQuantityComponentLayout.visibility = View.VISIBLE
        binding.productPlusQuantityButtonDefault.visibility = View.GONE
        binding.productQuantityComponent.quantity = quantity
    }

    companion object {
        fun of(
            parent: ViewGroup,
            productListener: ProductQuantityClickListener,
        ): RecommendProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendProductBinding.inflate(layoutInflater, parent, false)
            return RecommendProductViewHolder(binding, productListener)
        }
    }
}
