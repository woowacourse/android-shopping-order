package woowacourse.shopping.view.recommend

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecommendProductsAdapter(
    private val recommendProductItemActions: RecommendProductItemActions,
) : RecyclerView.Adapter<RecommendProductsViewHolder>() {
    private var items: List<RecommendProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductsViewHolder =
        RecommendProductsViewHolder.of(
            parent = parent,
            recommendProductItemActions = recommendProductItemActions,
        )

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<RecommendProduct>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecommendProductsViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
