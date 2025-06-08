package woowacourse.shopping.view.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CouponAdapter :
    ListAdapter<CouponItem, CouponViewHolder>(
        object : DiffUtil.ItemCallback<CouponItem>() {
            override fun areItemsTheSame(
                oldItem: CouponItem,
                newItem: CouponItem,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CouponItem,
                newItem: CouponItem,
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
        holder.bind(getItem(position))
    }
}
