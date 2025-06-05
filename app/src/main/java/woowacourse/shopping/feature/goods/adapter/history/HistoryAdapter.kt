package woowacourse.shopping.feature.goods.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemHistoryBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener

class HistoryAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<Cart, HistoryViewHolder>(HistoryDiffUtil()) {
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
}
