package woowacourse.shopping.presentation.ui.curation

import woowacourse.shopping.presentation.base.CartCountHandler
import woowacourse.shopping.presentation.base.ProductActionHandler
import woowacourse.shopping.presentation.ui.shopping.ShoppingActionHandler

interface CurationActionHandler : ProductActionHandler {
    fun order()
}
