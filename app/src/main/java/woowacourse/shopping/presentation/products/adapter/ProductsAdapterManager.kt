package woowacourse.shopping.presentation.products.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapterManager(
    context: Context,
    spanCount: Int,
    private val adapter: RecyclerView.Adapter<*>,
    private val productViewType: Int,
) : GridLayoutManager(context, spanCount) {
    init {
        spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) != productViewType) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
    }
}
