package woowacourse.shopping.pay

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.Coupon

class CouponAdapter(
    private val onCheckClick: (Coupon, Int) -> Unit
) : RecyclerView.Adapter<CouponViewHolder>() {
    private val coupons: MutableList<Map<Coupon, Boolean>> = mutableListOf()

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

    fun updateCouponList(newCoupons: List<Map<Coupon, Boolean>>) {
        coupons.clear()
        coupons.addAll(newCoupons)
        notifyDataSetChanged()
    }
}
