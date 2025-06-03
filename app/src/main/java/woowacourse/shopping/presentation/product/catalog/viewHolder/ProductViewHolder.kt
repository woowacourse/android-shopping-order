package woowacourse.shopping.presentation.product.catalog.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ProductItemBinding
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandler

class ProductViewHolder(
    private val binding: ProductItemBinding,
    private val catalogHandler: CatalogEventHandler,
    private val handler: ProductQuantityHandler,
    private val onQuantityClick: (ProductUiModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: ProductUiModel) {
        binding.product = product
        binding.catalogHandler = catalogHandler
        binding.handler = handler
        binding.onQuantityClick = View.OnClickListener {
            onQuantityClick(product)
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(
            parent: ViewGroup,
            catalogHandler: CatalogEventHandler,
            handler: ProductQuantityHandler,
            onQuantityClick: (ProductUiModel) -> Unit,
        ): ProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ProductItemBinding.inflate(inflater, parent, false)
            binding.handler = handler
            binding.catalogHandler = catalogHandler

            return ProductViewHolder(binding, catalogHandler, handler, onQuantityClick)
        }
    }
}
