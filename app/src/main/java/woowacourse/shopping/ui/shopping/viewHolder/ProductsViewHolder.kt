package woowacourse.shopping.ui.shopping.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.repository.CartRepository
import woowacourse.shopping.databinding.ProductItemBinding
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.shopping.ProductItem
import woowacourse.shopping.ui.shopping.ProductsItemType

class ProductsViewHolder private constructor(
    private val binding: ProductItemBinding,
    private val onClickListener: ProductsOnClickListener,
) :
    ItemViewHolder(binding.root) {

    fun bind(productItemType: ProductsItemType, cartRepository: CartRepository) {
        val productItem = productItemType as? ProductItem ?: return
        binding.product = productItem.product
        binding.cartProduct = cartRepository.findById(productItem.product.id)?.toUIModel()
        binding.listener = onClickListener

        binding.addCartBtn.setOnClickListener {
            onClickListener.onAddCart(productItem.product.id, 1)
            binding.addCartBtn.visibility = View.GONE
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
