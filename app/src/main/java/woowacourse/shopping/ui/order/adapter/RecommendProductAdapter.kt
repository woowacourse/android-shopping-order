package woowacourse.shopping.ui.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.common.OnItemQuantityChangeListener

class RecommendProductAdapter(
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : RecyclerView.Adapter<RecommendProductViewHolder>() {
    private val recommendProducts = mutableListOf<Product>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HolderProductBinding.inflate(inflater, parent, false)
        return RecommendProductViewHolder(binding, onItemQuantityChangeListener)
    }

    override fun getItemCount(): Int {
        return recommendProducts.size
    }

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(recommendProducts[position])
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecommendProducts(newRecommendProducts: List<Product>) {
        recommendProducts.clear()
        recommendProducts.addAll(newRecommendProducts)
        notifyDataSetChanged()
    }
}
