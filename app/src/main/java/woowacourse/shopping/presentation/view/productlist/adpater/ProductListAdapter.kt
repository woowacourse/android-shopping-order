package woowacourse.shopping.presentation.view.productlist.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.view.productlist.ProductListener
import woowacourse.shopping.presentation.view.productlist.viewholder.ProductListViewHolder

class ProductListAdapter(
    private val productListener: ProductListener,
) : ListAdapter<CartProductModel, ProductListViewHolder>(productDiffUtil) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder(parent, productListener)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun getItemViewType(position: Int): Int = ViewType.PRODUCT_LIST.ordinal

    fun setItems(newItems: List<CartProductModel>) {
        submitList(newItems)
    }

    companion object {
        private val productDiffUtil = object : DiffUtil.ItemCallback<CartProductModel>() {
            override fun areItemsTheSame(oldItem: CartProductModel, newItem: CartProductModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CartProductModel, newItem: CartProductModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}