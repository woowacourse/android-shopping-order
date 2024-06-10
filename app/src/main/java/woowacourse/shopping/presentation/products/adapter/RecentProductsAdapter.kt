package woowacourse.shopping.presentation.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.presentation.products.ProductCountActionHandler
import woowacourse.shopping.presentation.products.uimodel.RecentProductUiModel

class RecentProductsAdapter(
    private val actionHandler: ProductCountActionHandler,
) :
    ListAdapter<RecentProductUiModel, RecentProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecentProductBinding.inflate(inflater, parent, false)
        binding.actionHandler = actionHandler
        return RecentProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<RecentProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: RecentProductUiModel,
                    newItem: RecentProductUiModel,
                ): Boolean {
                    return oldItem.product.id == newItem.product.id
                }

                override fun areContentsTheSame(
                    oldItem: RecentProductUiModel,
                    newItem: RecentProductUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
