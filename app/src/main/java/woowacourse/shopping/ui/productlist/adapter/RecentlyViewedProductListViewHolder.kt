package woowacourse.shopping.ui.productlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemRecentlyViewedProductBinding
import woowacourse.shopping.ui.productlist.uistate.RecentlyViewedProductUIState

class RecentlyViewedProductListViewHolder private constructor(
    private val binding: ItemRecentlyViewedProductBinding,
    private val onClick: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            onClick(binding.recentlyViewedProduct?.productId ?: return@setOnClickListener)
        }
    }

    fun bind(recentlyViewedProduct: RecentlyViewedProductUIState) {
        binding.recentlyViewedProduct = recentlyViewedProduct
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClick: (Long) -> Unit
        ): RecentlyViewedProductListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_recently_viewed_product,
                parent,
                false,
            )
            val binding = ItemRecentlyViewedProductBinding.bind(view)
            return RecentlyViewedProductListViewHolder(binding, onClick)
        }
    }
}
