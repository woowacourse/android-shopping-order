package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.products.adapter.OnClickProductItem
import woowacourse.shopping.ui.products.adapter.ProductsViewHolder
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import woowacourse.shopping.ui.utils.OnDecreaseProductQuantity
import woowacourse.shopping.ui.utils.OnIncreaseProductQuantity

class RecommendProductsAdapter(
    private val onClickProductItem: OnClickProductItem,
    private val onIncreaseProductQuantity: OnIncreaseProductQuantity,
    private val onDecreaseProductQuantity: OnDecreaseProductQuantity,
) :
    ListAdapter<ProductUiModel, ProductsViewHolder.ProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductsViewHolder.ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return ProductsViewHolder.ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductsViewHolder.ProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), onClickProductItem, onIncreaseProductQuantity, onDecreaseProductQuantity)
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<ProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean {
                    return oldItem.productId == newItem.productId
                }

                override fun areContentsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
