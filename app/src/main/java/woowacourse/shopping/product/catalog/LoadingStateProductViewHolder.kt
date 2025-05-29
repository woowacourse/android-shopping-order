package woowacourse.shopping.product.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ShimmerProductItemBinding

class LoadingStateProductViewHolder(
    val binding: ShimmerProductItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): LoadingStateProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ShimmerProductItemBinding.inflate(inflater, parent, false)
            return LoadingStateProductViewHolder(binding)
        }
    }
}