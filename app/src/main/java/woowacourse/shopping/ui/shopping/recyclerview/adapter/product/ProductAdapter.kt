package woowacourse.shopping.ui.shopping.recyclerview.adapter.product

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.ui.shopping.recyclerview.ShoppingViewType
import woowacourse.shopping.util.diffutil.ProductDiffUtil
import woowacourse.shopping.util.listener.CartProductClickListener
import woowacourse.shopping.widget.SkeletonCounterView

class ProductAdapter(
    private val cartProductClickListener: CartProductClickListener,
    private val counterListener: SkeletonCounterView.OnCountChangedListener,
) : ListAdapter<CartProductModel, ProductViewHolder>(ProductDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            parent = parent,
            cartProductClickListener = cartProductClickListener,
            counterListener = counterListener,
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = ShoppingViewType.PRODUCT.value
}
