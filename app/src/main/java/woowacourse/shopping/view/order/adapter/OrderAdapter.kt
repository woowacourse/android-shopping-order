package woowacourse.shopping.view.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import woowacourse.shopping.view.core.base.BaseViewHolder
import woowacourse.shopping.view.order.state.OrderUiState

class OrderAdapter(
    private val handler: Handler,
) : ListAdapter<OrderRvItems, BaseViewHolder<ViewBinding>>(OrderDiffUtil()) {
    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewBinding> {
        return when (OrderRvItems.ViewType.entries[viewType]) {
            OrderRvItems.ViewType.VIEW_TYPE_ORDER_TITLE -> OrderTitleViewHolder(parent)
            OrderRvItems.ViewType.VIEW_TYPE_COUPONS -> CouponViewHolder(parent, handler)
            OrderRvItems.ViewType.VIEW_TYPE_PAYMENT -> PaymentViewHolder(parent)
        }
    }

    fun submitItems(items: OrderUiState) {
        val newItems = mutableListOf<OrderRvItems>()

        newItems += OrderRvItems.OrderTitleItem
        newItems += items.coupons.map { OrderRvItems.CouponItem(it) }
        newItems += OrderRvItems.PaymentItem(items.payment)

        this.submitList(newItems)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            OrderRvItems.OrderTitleItem -> Unit
            is OrderRvItems.CouponItem -> (holder as CouponViewHolder).bind(item)
            is OrderRvItems.PaymentItem -> (holder as PaymentViewHolder).bind(item)
        }
    }

    interface Handler : CouponViewHolder.Handler
}
