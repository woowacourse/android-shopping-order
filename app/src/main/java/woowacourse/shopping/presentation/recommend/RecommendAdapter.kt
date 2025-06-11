package woowacourse.shopping.presentation.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.ProductUiModelDiffCallback

class RecommendAdapter(
    private val handler: ProductQuantityHandler,
    private val onQuantityClick: (ProductUiModel) -> Unit,
) : ListAdapter<ProductUiModel, RecommendViewHolder>(ProductUiModelDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder = RecommendViewHolder(parent, onQuantityClick, handler)

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.count()

    fun updateProduct(product: ProductUiModel) {
        val newList = currentList.toMutableList()
        val index = newList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            newList[index] = product
            submitList(newList)
        }
    }
}
