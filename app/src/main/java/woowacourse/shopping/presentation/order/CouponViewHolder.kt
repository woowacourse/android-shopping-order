package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    itemClickListener: CouponClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.itemClickListener = itemClickListener
    }

    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }

    companion object {
        fun create(
            parent: ViewGroup,
            itemClickListener: CouponClickListener,
        ): CouponViewHolder =
            CouponViewHolder(
                binding =
                    ItemCouponBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                itemClickListener = itemClickListener,
            )
    }
}
