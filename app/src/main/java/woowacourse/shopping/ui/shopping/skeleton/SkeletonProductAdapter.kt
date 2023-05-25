package woowacourse.shopping.ui.shopping.skeleton

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SkeletonProductAdapter : RecyclerView.Adapter<SkeletonProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonProductViewHolder {
        return SkeletonProductViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SkeletonProductViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return 15
    }
}
