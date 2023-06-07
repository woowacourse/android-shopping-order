package woowacourse.shopping.feature.main.product

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.CartProductUiModel

class MainProductAdapter(
    private val listener: ProductClickListener,
) : ListAdapter<CartProductUiModel, MainProductViewHolder>(ProductDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainProductViewHolder {
        return MainProductViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: MainProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    fun setItems(newItems: List<CartProductUiModel>) {
        submitList(newItems)
    }

    fun reBindItem(productId: Long) {
        val cartProductUiModel = currentList.find { it.productUiModel.id == productId }
        cartProductUiModel?.let {
            val index = currentList.indexOf(it)
            notifyItemChanged(index)
        }
    }

    companion object {
        const val VIEW_TYPE = 222

        private val ProductDiffUtil = object : DiffUtil.ItemCallback<CartProductUiModel>() {
            override fun areItemsTheSame(
                oldItem: CartProductUiModel,
                newItem: CartProductUiModel,
            ): Boolean {
                return oldItem.productUiModel.id == newItem.productUiModel.id
            }

            override fun areContentsTheSame(
                oldItem: CartProductUiModel,
                newItem: CartProductUiModel,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
