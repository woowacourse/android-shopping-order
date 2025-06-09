package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.view.payment.state.CouponUi
import woowacourse.shopping.view.payment.vm.PaymentViewModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponUi, viewModel: PaymentViewModel) {
        binding.coupon = item
        binding.vm = viewModel
        binding.executePendingBindings()
    }

    companion object {
        fun of(
            parent: ViewGroup,
        ): CouponViewHolder {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
            val binding = ItemCouponBinding.bind(inflater)
            return CouponViewHolder(binding)
        }
    }
}
