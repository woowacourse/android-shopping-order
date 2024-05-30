package woowacourse.shopping.ui.cart.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductSkeletonBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.products.LoadingUiModel
import woowacourse.shopping.ui.products.ProductUiModel
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.adapter.ProductDiffUtil
import woowacourse.shopping.ui.products.adapter.ProductSkeletonViewHolder
import woowacourse.shopping.ui.products.adapter.ProductViewHolder

class RecommendProductAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<ProductUiModel, RecyclerView.ViewHolder>(ProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val itemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val itemProductSkeletonBinding =
            ItemProductSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (ProductViewType.valueOfType(viewType)) {
            ProductViewType.PRODUCT -> RecommendProductViewHolder(itemProductBinding, viewModel)
            ProductViewType.SKELETON -> ProductSkeletonViewHolder(itemProductSkeletonBinding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (getItemViewType(position) == ProductViewType.PRODUCT.value) {
            (holder as ProductViewHolder).bind(
                getItem(position) as ProductWithQuantityUiModel,
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LoadingUiModel -> ProductViewType.SKELETON.value
            else -> ProductViewType.PRODUCT.value
        }
    }
}

enum class ProductViewType(val value: Int) {
    PRODUCT(0),
    SKELETON(1),
    ;

    companion object {
        fun valueOfType(value: Int): ProductViewType = if (value == 0) PRODUCT else SKELETON
    }
}
