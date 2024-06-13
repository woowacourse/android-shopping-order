package woowacourse.shopping.ui.home.adapter.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.ui.home.viewmodel.HomeViewModel

class RecentProductAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<RecentProduct, RecentProductViewHolder>(diffUtil) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder {
        return RecentProductViewHolder(
            ItemRecentProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        return holder.bind(currentList[position], viewModel)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<RecentProduct>() {
                override fun areContentsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ) = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ) = oldItem.productId == newItem.productId
            }
    }
}
