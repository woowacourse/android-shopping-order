package woowacourse.shopping.view.productlist

import androidx.recyclerview.widget.GridLayoutManager

class GridLayoutManagerSpanSizeLookup(val items: List<ProductListViewItem>) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        val isHeader = items[position].type == ProductListViewType.RECENT_VIEWED_ITEM
        val isFooter = items[position].type == ProductListViewType.SHOW_MORE_ITEM
        return if (isHeader || isFooter) {
            HEADER_FOOTER_SPAN
        } else {
            PRODUCT_ITEM_SPAN
        }
    }

    companion object {
        const val HEADER_FOOTER_SPAN = 2
        const val PRODUCT_ITEM_SPAN = 1
    }
}
