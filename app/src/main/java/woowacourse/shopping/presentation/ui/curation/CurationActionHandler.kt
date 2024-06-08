package woowacourse.shopping.presentation.ui.curation

import woowacourse.shopping.presentation.ui.shopping.ShoppingActionHandler

interface CurationActionHandler : ShoppingActionHandler {
    fun onOrderClick()
}
