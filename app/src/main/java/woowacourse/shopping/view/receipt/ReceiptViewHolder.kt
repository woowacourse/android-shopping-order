package woowacourse.shopping.view.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class ReceiptViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(couponItem: CouponItem) {
        binding.couponItem = couponItem
    }

    companion object {
        fun of(
            parent: ViewGroup,
        ): ReceiptViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiptViewHolder(binding)
        }
    }
}