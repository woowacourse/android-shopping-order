package woowacourse.shopping.presentation.ui.cart.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.presentation.ui.QuantityHandler

class RecommendAdapter(
    private var items: List<ProductListItem.ShoppingProductItem> = emptyList(),
    private val quantityHandler: QuantityHandler,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecommendBinding.inflate(inflater, parent, false)
        return RecommendViewHolder(binding, quantityHandler)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    fun updateItems(newItems: List<ProductListItem.ShoppingProductItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
