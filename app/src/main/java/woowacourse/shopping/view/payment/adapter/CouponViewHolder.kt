package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    eventHandler: EventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.handler = eventHandler
    }

    fun bind(
        coupon: Coupon,
        isSelected: Boolean,
    ) {
        binding.coupon = coupon
        binding.isSelected = isSelected
    }

    interface EventHandler {
        fun onSelectItem(item: Coupon)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventHandler: EventHandler,
        ): CouponViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(inflater, parent, false)
            return CouponViewHolder(binding, eventHandler)
        }
    }
}
