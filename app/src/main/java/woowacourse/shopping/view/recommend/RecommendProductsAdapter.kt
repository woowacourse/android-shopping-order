package woowacourse.shopping.view.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class RecommendProductsAdapter(
    private val recommendProductItemActions: RecommendProductItemActions,
) : ListAdapter<RecommendProduct, RecommendProductsViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductsViewHolder =
        RecommendProductsViewHolder.of(
            parent = parent,
            recommendProductItemActions = recommendProductItemActions,
        )

    override fun onBindViewHolder(
        holder: RecommendProductsViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<RecommendProduct>() {
                override fun areItemsTheSame(
                    oldItem: RecommendProduct,
                    newItem: RecommendProduct,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: RecommendProduct,
                    newItem: RecommendProduct,
                ): Boolean = oldItem == newItem
            }
    }
}
