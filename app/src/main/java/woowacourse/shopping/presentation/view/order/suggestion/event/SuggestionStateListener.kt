package woowacourse.shopping.presentation.view.order.suggestion.event

import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

interface SuggestionStateListener : QuantityChangeListener {
    fun onQuantitySelectorOpenButtonClick(productId: Long)
}
