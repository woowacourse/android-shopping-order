package woowacourse.shopping.presentation.purchase

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponListAdapter(
    private val actionHandler: PurchaseActionHandler,
) : ListAdapter<CouponUiModel, CouponListAdapter.CouponViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        Log.d("Purchase", "Created")
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        binding.actionHandler = actionHandler
        return CouponViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class CouponViewHolder(private val binding: ItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(couponUiModel: CouponUiModel) {
            binding.couponUiModel = couponUiModel
        }
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<CouponUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
