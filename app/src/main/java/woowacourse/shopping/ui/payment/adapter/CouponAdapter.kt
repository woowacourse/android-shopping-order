package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.data.coupon.CouponState
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.payment.CouponClickListener

class CouponAdapter(
    private val couponClickListener: CouponClickListener,
) : ListAdapter<CouponState, CouponViewHolder>(CouponDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, couponClickListener)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
