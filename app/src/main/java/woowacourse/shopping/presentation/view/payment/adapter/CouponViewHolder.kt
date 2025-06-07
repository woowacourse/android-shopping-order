package woowacourse.shopping.presentation.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.common.model.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    eventListener: CouponAdapter.CouponEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
    }

    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CouponAdapter.CouponEventListener,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, eventListener)
        }
    }
}
