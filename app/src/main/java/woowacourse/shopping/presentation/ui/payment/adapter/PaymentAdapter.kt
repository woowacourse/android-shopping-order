package woowacourse.shopping.presentation.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.ui.payment.PaymentActionHandler
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel

class PaymentAdapter(
    private val paymentActionHandler: PaymentActionHandler
) : ListAdapter<CouponUiModel, PaymentViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PaymentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return PaymentViewHolder(binding, paymentActionHandler)
    }

    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CouponUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean {
                    return oldItem.coupon.id == newItem.coupon.id
                }

                override fun areContentsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

class PaymentViewHolder(private val binding: ItemCouponBinding, val paymentActionHandler: PaymentActionHandler) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponUiModel) {
        binding.couponUiModel = item
        binding.paymentActionHandler = paymentActionHandler
    }
}
