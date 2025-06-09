package woowacourse.shopping.view.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    couponListener: CouponListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.couponListener = couponListener
    }

    fun bind(coupon: CouponItem) {
        val conditionText = coupon.getConditionText(binding.root.context)
        binding.couponCondition.text = conditionText
        binding.coupon = coupon
    }

    private fun CouponItem.getConditionText(context: Context): String {
        if (conditionResId == null) return ""
        return context.getString(conditionResId, *conditionArgs.toTypedArray())
    }

    companion object {
        fun of(
            parent: ViewGroup,
            couponListener: CouponListener,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, couponListener)
        }
    }

    fun interface CouponListener {
        fun onCouponClick(couponId: Int)
    }
}
