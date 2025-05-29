package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartSkeletonBinding

class CartSkeletonAdapter : RecyclerView.Adapter<CartSkeletonAdapter.SkeletonViewHolder>() {
    private var skeletonCount = 5

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SkeletonViewHolder {
        val binding =
            ItemCartSkeletonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return SkeletonViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SkeletonViewHolder,
        position: Int,
    ) {
        holder.binding.shimmerContainer.startShimmer()
    }

    override fun getItemCount(): Int = skeletonCount

    class SkeletonViewHolder(
        val binding: ItemCartSkeletonBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}
