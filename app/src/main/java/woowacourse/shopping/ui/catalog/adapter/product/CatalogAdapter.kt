package woowacourse.shopping.ui.catalog.adapter.product

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CatalogAdapter(
    private val onClickHandler: OnClickHandler,
) : ListAdapter<CatalogItem, CatalogItemViewHolder<CatalogItem, ViewDataBinding>>(
        object :
            DiffUtil.ItemCallback<CatalogItem>() {
            override fun areItemsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean = oldItem.catalogItemId == newItem.catalogItemId

            override fun areContentsTheSame(
                oldItem: CatalogItem,
                newItem: CatalogItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CatalogItemViewHolder<CatalogItem, ViewDataBinding> =
        when (CatalogItemViewType.entries[viewType]) {
            CatalogItemViewType.PRODUCT -> ProductViewHolder(parent, onClickHandler)
            CatalogItemViewType.LOAD_MORE -> LoadMoreViewHolder(parent, onClickHandler)
        } as CatalogItemViewHolder<CatalogItem, ViewDataBinding>

    override fun onBindViewHolder(
        holder: CatalogItemViewHolder<CatalogItem, ViewDataBinding>,
        position: Int,
    ) {
        val catalogItem: CatalogItem = getItem(position)
        holder.bind(catalogItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface OnClickHandler :
        ProductViewHolder.OnClickHandler,
        LoadMoreViewHolder.OnClickHandler
}
