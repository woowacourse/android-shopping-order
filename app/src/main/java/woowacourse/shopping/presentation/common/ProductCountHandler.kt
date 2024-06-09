package woowacourse.shopping.presentation.common

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct

interface ProductCountHandler {
    fun onPlus(cartProduct: CartProduct): Job

    fun onMinus(cartProduct: CartProduct): Job
}
