package woowacourse.shopping.feature.userInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderUiModel

class OrderHistoryAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<OrderUiModel, OrderHistoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = DataBindingUtil.inflate<ItemOrderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_order,
            parent,
            false
        )
        return OrderHistoryViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderUiModel>() {
            override fun areItemsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
