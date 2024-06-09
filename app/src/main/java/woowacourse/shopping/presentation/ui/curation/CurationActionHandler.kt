package woowacourse.shopping.presentation.ui.curation

import woowacourse.shopping.presentation.common.ProductClickHandler

interface CurationActionHandler : ProductClickHandler {
    fun order()
}
