package woowacourse.shopping.presentation.payment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.presentation.util.CouponDiffCallback

class CouponAdapter() : ListAdapter<Coupon, RecyclerView.ViewHolder>(CouponDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return CouponViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) = (holder as CouponViewHolder).bind(getItem(position))

    override fun getItemCount(): Int = currentList.size
}