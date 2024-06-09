package woowacourse.shopping.presentation.ui.curation

import woowacourse.shopping.presentation.base.ProductActionHandler

interface CurationActionHandler : ProductActionHandler {
    fun order()
}
