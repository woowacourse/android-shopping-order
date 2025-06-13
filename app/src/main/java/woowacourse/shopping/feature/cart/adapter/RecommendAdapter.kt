package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.CartProduct

class RecommendAdapter(
    private val recommendClickListener: RecommendClickListener,
) : ListAdapter<CartProduct, RecommendViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder = RecommendViewHolder.from(parent, recommendClickListener)

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        val item: CartProduct = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CartProduct>() {
                override fun areItemsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: CartProduct,
                    newItem: CartProduct,
                ): Boolean = oldItem == newItem
            }
    }
}
