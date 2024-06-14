package woowacourse.shopping.presentation.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.ui.CouponModel
import woowacourse.shopping.presentation.ui.payment.PaymentHandler
import woowacourse.shopping.presentation.util.ItemDiffCallback

class CouponAdapter(private val paymentHandler: PaymentHandler) :
    ListAdapter<CouponModel, CouponViewHolder>(CouponModelAdapterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, paymentHandler)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val CouponModelAdapterDiffCallback =
            ItemDiffCallback<CouponModel>(
                onItemsTheSame = { old, new ->
                    old.coupon.id == new.coupon.id
                },
                onContentsTheSame = { old, new ->
                    old == new
                },
            )
    }
}
