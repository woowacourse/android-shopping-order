package woowacourse.shopping.feature.goods.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemHistoryContainerBinding
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener
import woowacourse.shopping.feature.goods.adapter.GoodsRvItems

class HistoryContainerViewHolder(
    private val binding: ItemHistoryContainerBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: GoodsRvItems.HistoryItem) {
        val adapter = HistoryAdapter(goodsClickListener)
        adapter.submitList(model.items)
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            goodsClickListener: GoodsClickListener,
        ): HistoryContainerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHistoryContainerBinding.inflate(inflater, parent, false)
            return HistoryContainerViewHolder(binding, goodsClickListener)
        }
    }
}
