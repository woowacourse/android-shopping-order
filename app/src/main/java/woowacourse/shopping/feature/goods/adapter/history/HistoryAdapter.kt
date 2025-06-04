package woowacourse.shopping.feature.goods.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemHistoryBinding
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener

class HistoryAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<History, HistoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding, goodsClickListener)
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<History>() {
                override fun areItemsTheSame(
                    oldItem: History,
                    newItem: History,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: History,
                    newItem: History,
                ): Boolean = oldItem == newItem
            }
    }
}
