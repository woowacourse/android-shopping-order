package woowacourse.shopping.presentation.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemLoadMoreBinding

class LoadMoreViewHolder private constructor(
    private val binding: ItemLoadMoreBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(onClickLoadMore: () -> Unit) {
        binding.onClickLoadMore = onClickLoadMore
        binding.executePendingBindings()
    }

    companion object {
        fun create(parent: ViewGroup): LoadMoreViewHolder =
            LoadMoreViewHolder(
                binding =
                    ItemLoadMoreBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
            )
    }
}
