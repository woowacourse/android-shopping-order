package woowacourse.shopping.ui.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.products.ProductUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import woowacourse.shopping.ui.utils.ItemDiffCallback

class RecentProductAdapter(
    private val viewModel: ProductContentsViewModel,
) : ListAdapter<Product, RecentProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder {
        val binding =
            ItemRecentProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentProductViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = ItemDiffCallback<Product>(
            onItemsTheSame = { old, new -> old.id == new.id},
            onContentsTheSame = { old, new -> old == new },
        )
    }
}
