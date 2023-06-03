package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderUiModel

class OrderAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<OrderUiModel, OrderViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = DataBindingUtil.inflate<ItemOrderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_order,
            parent,
            false
        )
        return OrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
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
