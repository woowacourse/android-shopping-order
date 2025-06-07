package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemPaymentCouponBinding
import woowacourse.shopping.domain.model.Coupon

class PaymentCouponViewHolder private constructor(
    private val binding: ItemPaymentCouponBinding,
    onClickHandler: OnClickHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClickHandler = onClickHandler
    }

    fun bind(item: Coupon) {
        binding.coupon = item
    }

    fun interface OnClickHandler {
        fun onCouponSelected(couponId: Int)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClickHandler: OnClickHandler,
        ): PaymentCouponViewHolder {
            val inflate = ItemPaymentCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PaymentCouponViewHolder(inflate, onClickHandler)
        }
    }
}
