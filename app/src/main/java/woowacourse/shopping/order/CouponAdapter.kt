package woowacourse.shopping.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CouponAdapter(
    private val checkClickListener: CheckClickListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(
        object : DiffUtil.ItemCallback<CouponUiModel>() {
            override fun areItemsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    fun applyCoupon(coupon: Coupon) {
        val couponIndex = currentList.indexOfFirst { it.id == coupon.id }
        if (couponIndex == -1) return

        val newCouponItems =
            currentList.mapIndexed { index, coupon ->
                if (index == couponIndex) {
                    coupon.copy(isSelected = true)
                } else {
                    coupon.copy(isSelected = false)
                }
            }
        submitList(newCouponItems)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, checkClickListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }
}
