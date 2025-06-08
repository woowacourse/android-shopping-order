package woowacourse.shopping.presentation.view.checkout.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.view.checkout.CheckoutEventHandler

class CouponAdapter(
    private val checkoutEventHandler: CheckoutEventHandler,
) : ListAdapter<CouponUiModel, CouponViewHolder>(
        object : DiffUtil.ItemCallback<CouponUiModel>() {
            override fun areItemsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem.coupon.id == newItem.coupon.id

            override fun areContentsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, checkoutEventHandler)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
