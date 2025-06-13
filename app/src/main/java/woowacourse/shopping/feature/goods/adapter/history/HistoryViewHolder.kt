package woowacourse.shopping.feature.goods.adapter.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemHistoryBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener

class HistoryViewHolder(
    private val binding: ItemHistoryBinding,
    private val goodsClickListener: GoodsClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Product) {
        Log.d("dasdas", "HistoryViewHolder: $item")
        binding.model = item
        binding.goodsClickListener = goodsClickListener
    }

    companion object {
        fun of(
            parent: ViewGroup,
            goodsClickListener: GoodsClickListener,
        ): HistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHistoryBinding.inflate(inflater, parent, false)

            return HistoryViewHolder(binding, goodsClickListener)
        }
    }
}
