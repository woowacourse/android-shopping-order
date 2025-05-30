package woowacourse.shopping.view.cart.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.state.ProductState

class RecommendAdapter(
    private val handler: Handler,
    private val quantityHandler: CartQuantityHandler,
) : ListAdapter<ProductState, RecommendViewHolder>(RecommendDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        return RecommendViewHolder.of(parent, handler, quantityHandler)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    interface Handler : RecommendViewHolder.Handler
}
