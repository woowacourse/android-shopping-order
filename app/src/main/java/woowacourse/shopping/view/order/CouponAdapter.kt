package woowacourse.shopping.view.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponAdapter(
    private val couponItemClickListener: CouponItemClickListener,
) : ListAdapter<CouponViewItem, RecyclerView.ViewHolder>(CouponDiffUtil) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CouponViewItem.VIEW_TYPE_LOADING -> {
                val binding = ItemCartPlaceholderBinding.inflate(layoutInflater, parent, false)
                CouponLoadingViewHolder(binding)
            }
            else -> {
                val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
                CouponItemViewHolder(binding, couponItemClickListener)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is CouponItemViewHolder) {
            holder.bind(currentList[position] as CouponViewItem.CouponItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }
}
