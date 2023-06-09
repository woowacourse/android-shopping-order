package woowacourse.shopping.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderItemBinding
import woowacourse.shopping.ui.order.uistate.OrderItemUIState
import woowacourse.shopping.utils.PRICE_FORMAT

class OrderItemViewHolder private constructor(
    private val binding: ItemOrderItemBinding,
    private val onClickProduct: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClickProduct(binding.orderItem?.product?.id ?: return@setOnClickListener)
        }
    }

    fun bind(orderItem: OrderItemUIState) {
        binding.orderItem = orderItem
        Glide.with(itemView)
            .load(orderItem.product.imageUrl)
            .into(binding.ivProduct)
        binding.tvProductName.text = orderItem.product.name
        binding.tvProductPrice.text = itemView.context.getString(R.string.format_price)
            .format(PRICE_FORMAT.format(orderItem.product.price))
        binding.tvQuantity.text = itemView.context.getString(R.string.format_quantity)
            .format(orderItem.quantity)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickProduct: (Long) -> Unit
        ): OrderItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_item, parent, false)
            val binding = ItemOrderItemBinding.bind(view)
            return OrderItemViewHolder(
                binding,
                onClickProduct
            )
        }
    }
}
