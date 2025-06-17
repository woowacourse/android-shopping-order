package woowacourse.shopping.pay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.cart.CartViewHolder
import woowacourse.shopping.cart.CheckClickListener
import woowacourse.shopping.cart.DeleteProductClickListener
import woowacourse.shopping.databinding.CartItemBinding
import woowacourse.shopping.databinding.CouponItemBinding
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.product.catalog.QuantityControlListener

class CouponViewHolder(
    val binding: CouponItemBinding,
    onCheckClick: (Coupon, Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onCheckClick = onCheckClick
    }

    fun bind(
        coupon: Map<Coupon, Boolean>,
        position: Int,
    ) {
        binding.coupon = coupon.keys.first()
        binding.isChecked = coupon.getValue(coupon.keys.first())
        binding.position = position
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onCheckClick: (Coupon, Int) -> Unit,
        ): CouponViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CouponItemBinding.inflate(inflater, parent, false)
            return CouponViewHolder(
                binding,
                onCheckClick = onCheckClick
            )
        }
    }
}
