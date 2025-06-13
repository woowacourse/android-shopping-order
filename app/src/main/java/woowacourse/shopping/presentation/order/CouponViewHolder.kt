package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.uimodel.CouponUiModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    couponClickListener: CouponClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.clickListener = couponClickListener
    }

    fun bind(couponUiModel: CouponUiModel) {
        binding.couponUiModel = couponUiModel
    }

    companion object {
        fun create(
            parent: ViewGroup,
            couponClickListener: CouponClickListener,
        ): CouponViewHolder =
            CouponViewHolder(
                ItemCouponBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                couponClickListener = couponClickListener,
            )
    }
}
