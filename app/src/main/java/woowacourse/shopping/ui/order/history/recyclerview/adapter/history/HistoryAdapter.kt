package woowacourse.shopping.ui.order.history.recyclerview.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.UiOrderResponse

class HistoryAdapter(
    private val orderedProducts: List<UiOrderResponse>,
    private val onDetailListener: (Int) -> Unit,
) : RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, onDetailListener)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = orderedProducts[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return orderedProducts.size
    }
}
