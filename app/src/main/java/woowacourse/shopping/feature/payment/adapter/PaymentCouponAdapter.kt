package woowacourse.shopping.feature.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.feature.payment.adapter.PaymentCouponViewHolder.CouponClickListener

class PaymentCouponAdapter(
    private val couponClickListener: CouponClickListener,
) : ListAdapter<Coupon, PaymentCouponViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PaymentCouponViewHolder = PaymentCouponViewHolder.from(parent, couponClickListener)

    override fun onBindViewHolder(
        holder: PaymentCouponViewHolder,
        position: Int,
    ) {
        val item: Coupon = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Coupon>() {
                override fun areItemsTheSame(
                    oldItem: Coupon,
                    newItem: Coupon,
                ): Boolean = oldItem.couponDetail.id == newItem.couponDetail.id

                override fun areContentsTheSame(
                    oldItem: Coupon,
                    newItem: Coupon,
                ): Boolean = oldItem == newItem
            }
    }
}
