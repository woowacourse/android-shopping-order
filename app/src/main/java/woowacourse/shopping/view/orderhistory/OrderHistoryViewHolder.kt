package woowacourse.shopping.view.orderhistory

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderDetailModel
import java.time.format.DateTimeFormatter

class OrderHistoryViewHolder(
    private val binding: ItemOrderBinding,
    onItemClick: OrderHistoryAdapter.OnItemClick,
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onItemClick = onItemClick
    }

    fun bind(item: OrderDetailModel) {
        binding.order = item

        if (item.products.size > 1) {
            binding.textOrderName.text = binding.root.context.getString(
                R.string.order_multiple_name,
                item.products[0].name,
                item.products.size - 1,
            )
        } else {
            binding.textOrderName.text = item.products[0].name
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        binding.textOrderDatetime.text = formatter.format(item.orderedDateTime)
    }
}
