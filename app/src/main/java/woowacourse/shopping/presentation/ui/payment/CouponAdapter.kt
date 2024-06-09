package woowacourse.shopping.presentation.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponAdapter(private val clickListener: CouponEventHandler) :
    RecyclerView.Adapter<CouponViewHolder>() {
    private var coupons: List<CouponUiModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(coupons[position])
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    CouponAdapterPayload.IS_CHECKED_CHANGED -> {
                        holder.onIsCheckedChanged(coupons[position])
                    }

                    else -> {}
                }
            }
            holder.bind(coupons[position])
        }
    }

    override fun getItemCount(): Int = coupons.size

    fun submitCoupons(newCoupons: List<CouponUiModel>) {
        val hasInitialized = coupons.isEmpty()
        coupons = newCoupons
        if (hasInitialized) {
            notifyItemRangeInserted(0, newCoupons.size)
        }
    }

    fun updateChecked(updatedCouponIds: Set<Long>) {
        updatedCouponIds.forEach { couponId ->
            val updatedPosition = coupons.indexOfFirst { it.id == couponId }
            notifyItemChanged(updatedPosition, CouponAdapterPayload.IS_CHECKED_CHANGED)
        }
    }
}
