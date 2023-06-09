package woowacourse.shopping.ui.shopping.productAdapter.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.shopping.productAdapter.ProductsItemType
import woowacourse.shopping.ui.shopping.productAdapter.ProductsListener

class ProductsViewHolder private constructor(
    private val binding: ItemProductBinding,
    private val listener: ProductsListener
) : ShoppingViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(product: ProductsItemType.Product) {
        binding.item = product
        binding.btnProductCount.count = product.count
        setVisibility(binding.btnProductCount.count)

        binding.btnProductCount.setOnCountChangeListener { _, count ->
            listener.onAddCartOrUpdateCount(product.product.id, count)
            setVisibility(count)
        }
        binding.btnAddToCart.setOnClickListener {
            binding.btnProductCount.count = 1
            listener.onAddCartOrUpdateCount(product.product.id, binding.btnProductCount.count)
            setVisibility(binding.btnProductCount.count)
        }
    }

    private fun setVisibility(count: Int) {
        if (count == 0) {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.btnProductCount.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnProductCount.visibility = View.VISIBLE
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: ProductsListener): ProductsViewHolder {
            val binding = ItemProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductsViewHolder(binding, listener)
        }
    }
}
