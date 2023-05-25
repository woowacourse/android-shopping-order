package woowacourse.shopping.ui.basket.skeleton

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SkeletonBasketProductAdapter : RecyclerView.Adapter<SkeletonBasketProductViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkeletonBasketProductViewHolder {
        return SkeletonBasketProductViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SkeletonBasketProductViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return 15
    }
}
