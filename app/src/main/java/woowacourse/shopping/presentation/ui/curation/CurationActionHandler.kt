package woowacourse.shopping.presentation.ui.curation

import kotlinx.coroutines.Job
import woowacourse.shopping.presentation.base.ProductActionHandler

interface CurationActionHandler : ProductActionHandler {
    fun order()
}
