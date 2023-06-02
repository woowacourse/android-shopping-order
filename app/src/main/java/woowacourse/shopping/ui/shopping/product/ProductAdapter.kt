package woowacourse.shopping.ui.shopping.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.mapper.toProductDomainModel
import woowacourse.shopping.ui.model.ProductUiModel
import woowacourse.shopping.ui.shopping.ShoppingViewType

class ProductAdapter(
    private val onItemClick: (ProductUiModel) -> Unit,
    private val minusClickListener: (Product) -> Unit,
    private val plusClickListener: (Product) -> Unit,
    private val addClickListener: (Product) -> Unit
) : ListAdapter<ProductUiModel, ProductViewHolder>(productDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            parent,
            { onItemClick(currentList[it]) },
            { minusClickListener(it.toProductDomainModel()) },
            { plusClickListener(it.toProductDomainModel()) },
            { addClickListener(it.toProductDomainModel()) }
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = ShoppingViewType.PRODUCT.value

    companion object {
        private val productDiffUtil = object : DiffUtil.ItemCallback<ProductUiModel>() {
            override fun areItemsTheSame(oldItem: ProductUiModel, newItem: ProductUiModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ProductUiModel, newItem: ProductUiModel): Boolean =
                oldItem == newItem
        }
    }
}
