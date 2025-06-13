package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val onclickHandler: OnclickHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponUiModel) {
        binding.coupon = item
        binding.onclickHandler = onclickHandler
    }

    interface OnclickHandler {
        fun onCouponClick(id: Long)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onclickHandler: OnclickHandler,
        ): CouponViewHolder {
            val inflate =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(inflate, onclickHandler)
        }
    }
}
