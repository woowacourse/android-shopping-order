package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.view.payment.OnclickPayment
import woowacourse.shopping.view.payment.adapter.viewholder.CouponViewHolder

class CouponAdapter(
    private val onclickPayment: OnclickPayment,
) : ListAdapter<Coupon, CouponViewHolder>(CouponDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val view = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(view, onclickPayment)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    class CouponDiffCallback : DiffUtil.ItemCallback<Coupon>() {
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

    fun updateCoupons(coupons: List<Coupon>) {
        submitList(coupons)
    }
}
