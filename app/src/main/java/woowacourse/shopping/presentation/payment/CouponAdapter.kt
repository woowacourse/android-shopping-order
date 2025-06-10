package woowacourse.shopping.presentation.payment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.presentation.payment.event.CouponEventHandler
import woowacourse.shopping.presentation.util.CouponDiffCallback

class CouponAdapter(
    private val couponHandler: CouponEventHandler,
) : ListAdapter<Coupon, CouponViewHolder>(CouponDiffCallback()) {
    var selectedCouponId: Long? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder(parent, couponHandler)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        val isChecked = (item.id == selectedCouponId)
        holder.bind(item, isChecked)
    }

    fun couponCheck(newSelectedCouponId: Long?) {
        val oldSelectedCouponId = selectedCouponId
        selectedCouponId = newSelectedCouponId

        val oldPosition = currentList.indexOfFirst { it.id == oldSelectedCouponId }
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }

        val newPosition = currentList.indexOfFirst { it.id == newSelectedCouponId }
        if (newPosition != -1) {
            notifyItemChanged(newPosition)
        }
    }

    override fun getItemCount(): Int = currentList.size
}
