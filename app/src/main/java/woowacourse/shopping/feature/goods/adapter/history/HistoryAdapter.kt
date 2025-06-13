package woowacourse.shopping.feature.goods.adapter.history

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener

class HistoryAdapter(
    private val goodsClickListener: GoodsClickListener,
) : ListAdapter<Product, HistoryViewHolder>(HistoryDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryViewHolder = HistoryViewHolder.of(parent, goodsClickListener)

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int,
    ) {
        Log.d("dasdas", "onBindViewHolder: ${getItem(position)}")
        holder.bind(getItem(position))
    }
}
