package woowacourse.shopping.presentation.view.order.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.view.order.payment.CouponStateEventListener

class CouponViewHolder private constructor(
    private val binding: ItemCouponBinding,
    private val couponStateEventListener: CouponStateEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: DisplayModel<CouponUiModel>) {
        binding.coupon = item.data
        binding.isSelected = item.isSelected
        binding.stateListener = couponStateEventListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            couponStateEventListener: CouponStateEventListener,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, couponStateEventListener)
        }
    }
}
