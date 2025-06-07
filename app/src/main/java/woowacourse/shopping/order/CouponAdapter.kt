package woowacourse.shopping.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CouponAdapter :
    ListAdapter<Coupon, CouponViewHolder>(
        object : DiffUtil.ItemCallback<Coupon>() {
            override fun areItemsTheSame(
                oldItem: Coupon,
                newItem: Coupon,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Coupon,
                newItem: Coupon,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }
}
