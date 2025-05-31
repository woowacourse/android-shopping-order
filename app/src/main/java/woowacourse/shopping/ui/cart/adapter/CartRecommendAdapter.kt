package woowacourse.shopping.ui.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.cart.adapter.CartRecommendViewHolder.OnClickHandler

class CartRecommendAdapter(
    private val onClickHandler: OnClickHandler,
) : ListAdapter<Product, CartRecommendViewHolder>(CartRecommendDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartRecommendViewHolder = CartRecommendViewHolder.from(parent, onClickHandler)

    override fun onBindViewHolder(
        holder: CartRecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
