package woowacourse.shopping.presentation.recommend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.RecommendItemBinding
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class RecommendViewHolder(
    parent: ViewGroup,
    private val onQuantityClick: (ProductUiModel) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.recommend_item, parent, false)
) {
    private val binding = RecommendItemBinding.bind(itemView)

    fun bind(productUiModel: ProductUiModel) {
        binding.product = productUiModel
        binding.onQuantityClick = View.OnClickListener {
            onQuantityClick(productUiModel)
        }
    }
}