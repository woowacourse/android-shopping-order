package woowacourse.shopping.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.products.ProductCountActionHandler
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel

class RecommendAdapter(
    private val actionHandler: ProductCountActionHandler,
) :
    ListAdapter<ProductUiModel, RecommendAdapter.RecommendViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return RecommendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), actionHandler)
    }

    class RecommendViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            uiModel: ProductUiModel,
            actionHandler: ProductCountActionHandler,
        ) {
            binding.productUiModel = uiModel
            binding.actionHandler = actionHandler
        }
    }

    companion object {
        private val diffCallback =
            object : ItemCallback<ProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
