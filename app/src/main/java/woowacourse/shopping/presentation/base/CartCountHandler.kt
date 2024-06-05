package woowacourse.shopping.presentation.base

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct

interface CartCountHandler {
    fun onPlus(cartProduct: CartProduct): Job

    fun onMinus(cartProduct: CartProduct): Job
}
