package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.model.PageModel

fun Page.toUi(hasPrevious: Boolean, hasNext: Boolean): PageModel =
    PageModel(value = value, hasPrevious = hasPrevious, hasNext = hasNext)
