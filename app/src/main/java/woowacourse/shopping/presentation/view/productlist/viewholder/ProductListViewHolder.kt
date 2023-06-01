package woowacourse.shopping.presentation.view.productlist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemProductListBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.custom.CountView
import woowacourse.shopping.presentation.view.productlist.ProductListener

class ProductListViewHolder(
    parent: ViewGroup,
    productListener: ProductListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from((parent.context))
        .inflate(R.layout.item_product_list, parent, false),
) {
    private val binding = ItemProductListBinding.bind(itemView)

    init {
        binding.productListener = productListener

        binding.productCountView.countStateChangeListener =
            object : CountView.OnCountStateChangeListener {
                override fun onCountChanged(countView: CountView?, count: Int) {
                    binding.cartModel?.let { productListener.onCountClick(it.product.id, count) }
                }
            }
    }

    fun bind(product: CartModel) {
        binding.cartModel = product
        binding.productCountView.setCount(product.count)
    }
}
