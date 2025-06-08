package woowacourse.shopping.view.main.adapter.recent

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.main.adapter.HistoryViewHolder

class RecentProductAdapter(
    private val items: List<Product>,
    private val handler: HistoryViewHolder.Handler,
) : RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryViewHolder {
        return HistoryViewHolder.of(parent, handler)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }
}
