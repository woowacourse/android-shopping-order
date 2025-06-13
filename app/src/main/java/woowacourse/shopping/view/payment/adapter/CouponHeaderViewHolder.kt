package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponHeaderBinding

class CouponHeaderViewHolder(
    binding: ItemCouponHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): CouponHeaderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponHeaderBinding.inflate(inflater, parent, false)
            return CouponHeaderViewHolder(binding)
        }
    }
}
