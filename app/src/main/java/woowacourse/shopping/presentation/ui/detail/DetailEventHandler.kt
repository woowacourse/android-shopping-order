package woowacourse.shopping.presentation.ui.detail

interface DetailEventHandler {
    fun onAddProductClicked(productId: Long)

    fun onRecentProductClicked(productId: Long)

    fun onBackButtonClicked()
}
