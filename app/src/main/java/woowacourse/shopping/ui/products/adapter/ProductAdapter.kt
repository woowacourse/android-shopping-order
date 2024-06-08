package woowacourse.shopping.ui.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductSkeletonBinding
import woowacourse.shopping.ui.products.uimodel.LoadingUiModel
import woowacourse.shopping.ui.products.uimodel.ProductUiModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import woowacourse.shopping.ui.utils.ItemDiffCallback

class ProductAdapter(
    private val viewModel: ProductContentsViewModel,
) : ListAdapter<ProductUiModel, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val itemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val itemProductSkeletonBinding =
            ItemProductSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (ProductViewType.valueOfType(viewType)) {
            ProductViewType.PRODUCT -> ProductViewHolder(itemProductBinding, viewModel)
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

    companion object {
        val diffCallback = ItemDiffCallback<ProductUiModel>(
            onItemsTheSame = { old, new -> old == new},
            onContentsTheSame = { old, new -> old == new },
        )
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
