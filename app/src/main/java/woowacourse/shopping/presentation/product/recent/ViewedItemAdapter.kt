package woowacourse.shopping.presentation.product.recent

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandlerImpl
import woowacourse.shopping.presentation.util.ProductUiModelDiffCallback

class ViewedItemAdapter(
    private val handler: CatalogEventHandlerImpl,
) : ListAdapter<ProductUiModel, RecyclerView.ViewHolder>(ProductUiModelDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewedItemHolder = ViewedItemHolder.from(parent, handler)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        (holder as ViewedItemHolder).bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size
}
