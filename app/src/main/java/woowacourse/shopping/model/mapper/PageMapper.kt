package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.page.DomainPage
import woowacourse.shopping.model.PageModel

fun DomainPage.toUi(hasPrevious: Boolean, hasNext: Boolean): PageModel =
    PageModel(value = value, hasPrevious = hasPrevious, hasNext = hasNext)
