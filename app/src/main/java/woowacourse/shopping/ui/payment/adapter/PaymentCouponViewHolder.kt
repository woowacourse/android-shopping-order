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
    private lateinit var item: Coupon

    init {
        binding.onClickHandler = onClickHandler
    }

    fun bind(item: Coupon) {
        this.item = item
        binding.coupon = this.item
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
