package woowacourse.shopping.ui.shopping.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.databinding.ProductItemBinding
import woowacourse.shopping.ui.shopping.ProductItem
import woowacourse.shopping.ui.shopping.ProductsItemType

class ProductsViewHolder private constructor(
    private val binding: ProductItemBinding,
    private val onClickListener: ProductsOnClickListener,
) :
    ItemViewHolder(binding.root) {

    fun bind(productItemType: ProductsItemType) {
        val productItem = productItemType as? ProductItem ?: return
        binding.product = productItem.product
        binding.count = productItem.count
        binding.listener = onClickListener

        binding.addCartBtn.setOnClickListener {
            onClickListener.onAddCart(productItem.product.id, 1)
            binding.count = binding.count + 1
            binding.executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,

            onClickListener: ProductsOnClickListener,
        ): ProductsViewHolder {
            val binding = ProductItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductsViewHolder(
                binding,
                onClickListener,
            )
        }
    }
}
