package woowacourse.shopping.presentation.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.cart.CartItemDiffCallback
import woowacourse.shopping.presentation.model.CartItemUiModel

class RecommendAdapter(
    private val itemClickListener: RecommendItemClickListener,
    private val counterClickListener: CartCounterClickListener,
) : ListAdapter<CartItemUiModel, RecommendViewHolder>(CartItemDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder = RecommendViewHolder.create(parent, itemClickListener, counterClickListener)

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }
}
