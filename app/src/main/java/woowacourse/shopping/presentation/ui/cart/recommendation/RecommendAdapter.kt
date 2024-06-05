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

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    RecommendAdapterPayload.QUANTITY_CHANGED -> {
                        holder.onQuantityChanged(recommendItems[position])
                    }

                    else -> {}
                }
            }
        }
    }

    override fun getItemCount(): Int = recommendItems.size

    fun submitItems(newItems: List<ShoppingProduct>) {
        val hasInitialized = recommendItems.isEmpty()
        recommendItems = newItems
        if (hasInitialized) {
            notifyItemRangeInserted(0, newItems.size)
        }
    }

    fun updateItems(updatedIds: Set<Long>) {
        updatedIds.forEach { productId ->
            val updatedPosition = recommendItems.indexOfFirst { it.product.id == productId }
            notifyItemChanged(updatedPosition, RecommendAdapterPayload.QUANTITY_CHANGED)
        }
    }
}
