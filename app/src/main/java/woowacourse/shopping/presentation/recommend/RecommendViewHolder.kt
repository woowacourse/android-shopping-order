package woowacourse.shopping.presentation.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.RecommendItemBinding
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandler

class RecommendViewHolder(
    parent: ViewGroup,
    catalogEventHandler: CatalogEventHandler,
    quantityHandler: ProductQuantityHandler,
) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recommend_item, parent, false),
    ) {
    private val binding = RecommendItemBinding.bind(itemView)

    init {
        binding.catalogHandler = catalogEventHandler
        binding.quantityHandler = quantityHandler
    }

    fun bind(productUiModel: ProductUiModel) {
        binding.product = productUiModel
    }
}
