package woowacourse.shopping.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PaymentAdapter : ListAdapter<PaymentItem, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (PaymentItem.ViewType.entries[viewType]) {
            PaymentItem.ViewType.COUPON_HEADER -> CouponHeaderViewHolder.from(parent)
            PaymentItem.ViewType.COUPON -> CouponViewHolder.from(parent)
            PaymentItem.ViewType.PAYMENT_INFORMATION -> PaymentInformationViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            PaymentItem.CouponHeaderItem -> Unit
            is PaymentItem.CouponItem ->
                (holder as CouponViewHolder).bind(
                    item.coupon,
                    item.isSelected,
                )

            PaymentItem.PaymentInformationItem -> {}
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<PaymentItem>() {
                override fun areItemsTheSame(
                    oldItem: PaymentItem,
                    newItem: PaymentItem,
                ): Boolean =
                    when {
                        oldItem is PaymentItem.CouponItem &&
                            newItem is PaymentItem.CouponItem -> oldItem.coupon.id == newItem.coupon.id

                        oldItem is PaymentItem.CouponHeaderItem &&
                            newItem is PaymentItem.CouponHeaderItem -> true

                        oldItem is PaymentItem.PaymentInformationItem &&
                            newItem is PaymentItem.PaymentInformationItem -> true

                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: PaymentItem,
                    newItem: PaymentItem,
                ): Boolean = oldItem == newItem
            }
    }
}
