package woowacourse.shopping.presentation.view.order.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponViewHolder private constructor(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponUiModel) {
        binding.coupon = item
    }

    companion object {
        fun from(parent: ViewGroup): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding)
        }
    }
}
