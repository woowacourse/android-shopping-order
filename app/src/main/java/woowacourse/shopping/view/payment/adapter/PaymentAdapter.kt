package woowacourse.shopping.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PaymentAdapter(
    private val couponSelectListener: CouponSelectListener,
) : ListAdapter<CouponItem, PaymentViewHolder>(
        object : DiffUtil.ItemCallback<CouponItem>() {
            override fun areItemsTheSame(
                oldItem: CouponItem,
                newItem: CouponItem,
            ): Boolean {
                return oldItem.coupon.id == newItem.coupon.id
            }

            override fun areContentsTheSame(
                oldItem: CouponItem,
                newItem: CouponItem,
            ): Boolean {
                return oldItem == newItem
            }
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PaymentViewHolder = PaymentViewHolder.from(parent, couponSelectListener)

    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
