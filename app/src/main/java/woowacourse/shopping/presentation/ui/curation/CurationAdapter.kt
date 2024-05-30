package woowacourse.shopping.presentation.ui.curation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewHolder
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewType

class CurationAdapter(
    private val curationActionHandler: CurationActionHandler,
) : ListAdapter<CartProduct, ShoppingViewHolder.ProductViewHolder>(DIFF_CALLBACK) {
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - LOADING_OFFSET) ShoppingViewType.LoadMore.value else ShoppingViewType.Product.value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingViewHolder.ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return ShoppingViewHolder.ProductViewHolder(binding, curationActionHandler)
    }

    override fun onBindViewHolder(
        holder: ShoppingViewHolder.ProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val LOADING_OFFSET = 1
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CartProduct>() {
                override fun areItemsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean {
                    return oldItem.productId == newItem.productId
                }

                override fun areContentsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
