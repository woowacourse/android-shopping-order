package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.data.coupon.CouponState
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponAdapter : ListAdapter<CouponState, CouponViewHolder>(CouponDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
