package woowacourse.shopping.view.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.view.order.adapter.viewholder.CouponViewHolder

class CouponAdapter(
    private val onClickCoupon: OnClickCoupon,
) : ListAdapter<Coupon, CouponViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, onClickCoupon)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val coupon = getItem(position)
        holder.bind(coupon)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Coupon>() {
                override fun areItemsTheSame(
                    oldItem: Coupon,
                    newItem: Coupon,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Coupon,
                    newItem: Coupon,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
