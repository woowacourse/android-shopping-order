package woowacourse.shopping.feature.order.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderPreviewBinding
import woowacourse.shopping.model.OrderPreviewUiModel
import woowacourse.shopping.util.toMoneyFormat

class OrderListViewHolder(
    private val binding: ItemOrderPreviewBinding,
    private val onClickItem: (Int) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.textGoOrderDetail.setOnClickListener {
            onClickItem(bindingAdapterPosition)
        }
    }

    fun bind(orderPreview: OrderPreviewUiModel) {
        val context = this.itemView.context
        binding.orderPreview = orderPreview
        binding.textOrderDate.text = orderPreview.toDateFormat()
        if (orderPreview.extraProductCount == 0) {
            binding.textProductName.text = orderPreview.mainName
        } else {
            binding.textProductName.text = context.getString(
                R.string.order_main_product_name_more_than_one,
                orderPreview.mainName,
                orderPreview.extraProductCount,
            )
        }
        binding.textOrderId.text = context.getString(R.string.order_id, orderPreview.orderId)
        binding.textPayPrice.text =
            context.getString(R.string.price_format, orderPreview.paymentAmount.toMoneyFormat())
    }

    companion object {
        fun create(parent: ViewGroup, onClickItem: (Int) -> Unit): OrderListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderPreviewBinding.inflate(inflater, parent, false)
            return OrderListViewHolder(binding, onClickItem)
        }
    }
}
