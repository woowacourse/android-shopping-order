package woowacourse.shopping.ui.cart.recommend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel
import woowacourse.shopping.ui.utils.ItemDiffCallback

class RecommendProductAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<ProductWithQuantityUiModel, RecommendProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder {
        val itemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RecommendProductViewHolder(itemProductBinding, viewModel)
    }

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        val diffCallback =
            ItemDiffCallback<ProductWithQuantityUiModel>(
                onItemsTheSame = { old, new -> old == new },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}
