package woowacourse.shopping.presentation.view.order.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    selectCouponListener: CouponAdapter.SelectCouponListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = selectCouponListener
    }

    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }

    companion object {
        fun from(
            parent: ViewGroup,
            selectCouponListener: CouponAdapter.SelectCouponListener,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, selectCouponListener)
        }
    }
}
