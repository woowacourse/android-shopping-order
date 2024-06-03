package woowacourse.shopping.ui.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductSkeletonBinding
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.LoadingUiModel
import woowacourse.shopping.ui.products.ProductItemClickListener
import woowacourse.shopping.ui.products.ProductUiModel
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel
import woowacourse.shopping.ui.utils.AddCartClickListener

class ProductAdapter(
    private val countButtonClickListener: CountButtonClickListener,
    private val addCartClickListener: AddCartClickListener,
    private val productItemClickListener: ProductItemClickListener,
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
            ProductViewType.PRODUCT ->
                ProductViewHolder(
                    itemProductBinding,
                    countButtonClickListener,
                    addCartClickListener,
                    productItemClickListener,
                )

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
