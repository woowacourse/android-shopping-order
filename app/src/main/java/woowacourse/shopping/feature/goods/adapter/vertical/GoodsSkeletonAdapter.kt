package woowacourse.shopping.feature.goods.adapter.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemGoodsSkeletonBinding

class GoodsSkeletonAdapter : RecyclerView.Adapter<GoodsSkeletonAdapter.SkeletonViewHolder>() {
    private val skeletonCount = 20 //

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SkeletonViewHolder {
        val binding =
            ItemGoodsSkeletonBinding.inflate(
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
        holder.binding.shimmerFrame.startShimmer()
    }

    override fun getItemCount(): Int = skeletonCount

    class SkeletonViewHolder(
        val binding: ItemGoodsSkeletonBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}
