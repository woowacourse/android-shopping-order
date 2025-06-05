package woowacourse.shopping.view.order.adapter

import androidx.recyclerview.widget.DiffUtil

class OrderDiffUtil : DiffUtil.ItemCallback<OrderRvItems>() {
    override fun areItemsTheSame(
        oldItem: OrderRvItems,
        newItem: OrderRvItems,
    ): Boolean {
        if (oldItem::class != newItem::class) return false

        return when (oldItem) {
            is OrderRvItems.OrderTitleItem -> true
            is OrderRvItems.CouponItem -> {
                newItem as OrderRvItems.CouponItem
                oldItem.coupon.checked == oldItem.coupon.checked
            }

            is OrderRvItems.PaymentItem -> {
                newItem as OrderRvItems.PaymentItem

                val oldItemTotalPayment = oldItem.value.totalPayment
                val newItemTotalPayment = newItem.value.totalPayment

                oldItemTotalPayment == newItemTotalPayment
            }
        }
    }

    override fun areContentsTheSame(
        oldItem: OrderRvItems,
        newItem: OrderRvItems,
    ): Boolean {
        if (oldItem::class != newItem::class) return false

        return when (oldItem) {
            is OrderRvItems.OrderTitleItem -> true

            is OrderRvItems.PaymentItem,
            is OrderRvItems.CouponItem,
            -> newItem == oldItem
        }
    }
}
