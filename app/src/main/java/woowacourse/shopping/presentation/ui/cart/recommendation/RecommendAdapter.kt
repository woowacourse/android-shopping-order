package woowacourse.shopping.presentation.ui.cart.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.ShoppingProduct

class RecommendAdapter(
    private val recommendItemCountHandler: RecommendItemCountHandler,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    private var recommendItems: List<ShoppingProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(recommendItems[position], recommendItemCountHandler)
    }

    override fun getItemCount(): Int = recommendItems.size

    fun loadData(recommendItems: List<ShoppingProduct>) {
        this.recommendItems = recommendItems
        notifyItemRangeChanged(0, recommendItems.size)
    }
}
