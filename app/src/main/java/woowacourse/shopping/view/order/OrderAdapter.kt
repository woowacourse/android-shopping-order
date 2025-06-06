package woowacourse.shopping.view.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class OrderAdapter(
    private val couponClickListener: CouponClickListener,
) : ListAdapter<CouponState, CouponViewHolder>(
        object : DiffUtil.ItemCallback<CouponState>() {
            override fun areItemsTheSame(
                oldItem: CouponState,
                newItem: CouponState,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CouponState,
                newItem: CouponState,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.of(parent, couponClickListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

interface CouponClickListener {
    fun onCouponClick(couponState: CouponState)
}
