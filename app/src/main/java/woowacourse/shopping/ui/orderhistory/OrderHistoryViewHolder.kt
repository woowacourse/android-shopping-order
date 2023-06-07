package woowacourse.shopping.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.ui.model.OrderUiModel
import woowacourse.shopping.util.setImage

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryBinding,
    private val onClicked: (order: OrderUiModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderUiModel) {
        with(binding) {
            this.order = order
            ivOrderRepresentProduct.setImage(
                order.products
                    .first()
                    .imageUrl
            )
            tvOrderProducts.text = root.context.getString(
                R.string.tv_order_products,
                order.products.first().name,
                order.products.size - COUNT_DIFF
            )
            root.setOnClickListener {
                onClicked(order)
            }
        }
    }

    companion object {
        private const val COUNT_DIFF = 1

        fun from(
            parent: ViewGroup,
            onClicked: (order: OrderUiModel) -> Unit,
        ): OrderHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemOrderHistoryBinding.inflate(layoutInflater, parent, false)

            return OrderHistoryViewHolder(
                binding = binding,
                onClicked = onClicked
            )
        }
    }
}
