package woowacourse.shopping.view.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class ReceiptViewHolder(
    private val binding: ItemCouponBinding,
    private val receiptActions: ReceiptActions,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(couponItem: CouponItem) {
        binding.couponItem = couponItem
        binding.checkboxCoupon.setOnClickListener {
            if (binding.checkboxCoupon.isChecked) {
                receiptActions.select(couponItem)
            } else {
                receiptActions.unselect()
            }
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            receiptActions: ReceiptActions
        ): ReceiptViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiptViewHolder(binding, receiptActions)
        }
    }
}