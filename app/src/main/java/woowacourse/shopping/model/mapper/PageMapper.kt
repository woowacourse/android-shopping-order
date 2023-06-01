package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.page.DomainPage
import woowacourse.shopping.model.PageModel

fun DomainPage.toUi(): PageModel = PageModel(value = value)
