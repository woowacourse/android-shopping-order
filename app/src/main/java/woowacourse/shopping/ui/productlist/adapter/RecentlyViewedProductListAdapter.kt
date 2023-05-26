package woowacourse.shopping.ui.productlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.productlist.uistate.RecentlyViewedProductUIState

class RecentlyViewedProductListAdapter(
    private val recentlyViewedProducts: List<RecentlyViewedProductUIState>,
    private val onClick: (Long) -> Unit,
) : RecyclerView.Adapter<RecentlyViewedProductListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentlyViewedProductListViewHolder {
        return RecentlyViewedProductListViewHolder.from(parent, onClick)
    }

    override fun getItemCount(): Int = recentlyViewedProducts.size

    override fun onBindViewHolder(holder: RecentlyViewedProductListViewHolder, position: Int) {
        holder.bind(recentlyViewedProducts[position])
    }
}
