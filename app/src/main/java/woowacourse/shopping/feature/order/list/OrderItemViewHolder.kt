package woowacourse.shopping.feature.order.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderMinInfoBinding
import woowacourse.shopping.model.OrderMinInfoItemUiModel
import woowacourse.shopping.util.toMoneyFormat
import java.time.format.DateTimeFormatter

class OrderItemViewHolder(
    private val binding: ItemOrderMinInfoBinding,
    orderItemClickListener: OrderItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.listener = orderItemClickListener
    }

    fun bind(orderMinInfoItemUiModel: OrderMinInfoItemUiModel) {
        binding.orderItem = orderMinInfoItemUiModel
        binding.orderDateTextView.text = orderMinInfoItemUiModel.date.format(dateTimeFormatter)
        binding.orderItemPaymentPrice.text =
            binding.orderItemPaymentPrice.context.getString(
                R.string.price_format,
                orderMinInfoItemUiModel.price.toMoneyFormat()
            )
    }

    companion object {
        private const val dateTimePattern = "yyyy.M.d"
        private val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

        fun create(
            parent: ViewGroup,
            orderItemClickListener: OrderItemClickListener
        ): OrderItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderMinInfoBinding.inflate(layoutInflater, parent, false)
            return OrderItemViewHolder(binding, orderItemClickListener)
        }
    }
}
