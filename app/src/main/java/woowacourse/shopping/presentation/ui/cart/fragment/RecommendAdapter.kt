package woowacourse.shopping.presentation.ui.cart.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.presentation.ui.cart.CartHandler
import woowacourse.shopping.presentation.ui.model.ProductModel

class RecommendAdapter(
    private var items: List<ProductModel> = emptyList(),
    private val cartHandler: CartHandler,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecommendBinding.inflate(inflater, parent, false)
        return RecommendViewHolder(binding, cartHandler)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    fun updateItems(newItems: List<ProductModel>) {
        items = newItems
        notifyItemRangeChanged(0, newItems.size)
    }
}
