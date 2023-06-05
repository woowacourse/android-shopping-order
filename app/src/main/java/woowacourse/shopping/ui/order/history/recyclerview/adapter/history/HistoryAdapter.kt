package woowacourse.shopping.ui.order.history.recyclerview.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.UiOrderResponse
import woowacourse.shopping.util.diffutil.OrderDiffUtil

class HistoryAdapter(
    private val onDetailListener: (Int) -> Unit,
) : ListAdapter<UiOrderResponse, HistoryViewHolder>(OrderDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, onDetailListener)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
