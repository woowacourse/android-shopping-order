package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon

class CouponAdapter: ListAdapter<Coupon, CouponViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateCoupons(newCoupons: List<Coupon>) {
        submitList(newCoupons)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Coupon>() {
            override fun areItemsTheSame(
                oldItem: Coupon,
                newItem: Coupon,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Coupon,
                newItem: Coupon,
            ): Boolean = oldItem == newItem
        }
    }
}
