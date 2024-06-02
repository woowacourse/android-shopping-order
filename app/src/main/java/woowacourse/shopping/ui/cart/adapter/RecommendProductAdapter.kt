package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel
import woowacourse.shopping.ui.utils.AddCartClickListener

class RecommendProductAdapter(
    private val countButtonClickListener: CountButtonClickListener,
    private val addCartClickListener: AddCartClickListener,
) : ListAdapter<ProductWithQuantityUiModel, RecommendProductViewHolder>(RecommendProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder {
        val itemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RecommendProductViewHolder(
            itemProductBinding,
            countButtonClickListener,
            addCartClickListener,
        )
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
}
