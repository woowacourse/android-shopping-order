package woowacourse.shopping.pay

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.Coupon

class CouponAdapter(
    private val coupons: List<Map<Coupon, Boolean>>,
    private val onCheckClick: (Coupon, Int) -> Unit
) : RecyclerView.Adapter<CouponViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CouponViewHolder =
        CouponViewHolder.from(parent, onCheckClick)


    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int
    ) {
        holder.bind(coupons[position], position)
    }

    override fun getItemCount(): Int = coupons.size

    fun updateCouponList(newCoupons: List<Coupon>, position: Int) {}
}
