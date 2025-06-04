package woowacourse.shopping.feature.goods.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemHistoryContainerBinding
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener

class HistoryContainerViewHolder(
    private val binding: ItemHistoryContainerBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(histories: List<History>) {
        val adapter = HistoryAdapter(goodsClickListener)
        adapter.submitList(histories)
        binding.rvHistory.adapter = adapter
        binding.rvHistory.itemAnimator = null
        binding.rvHistory.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            goodsClickListener: GoodsClickListener,
        ): HistoryContainerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHistoryContainerBinding.inflate(inflater, parent, false)
            return HistoryContainerViewHolder(binding, goodsClickListener)
        }
    }
}
