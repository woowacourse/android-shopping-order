package woowacourse.shopping.feature.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.model.Cart

class RecommendAdapter : RecyclerView.Adapter<RecommendViewHolder>() {
    private val items: MutableList<Cart> = mutableListOf()

    fun setItems(newItems: List<Cart>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder = RecommendViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        val item: Cart = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}
