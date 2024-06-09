package woowacourse.shopping.ui.cart.cartitem.uimodel

import android.util.Log
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response

sealed interface CartError {
    data object UpdateCart : CartError

    data object LoadCart : CartError

    data object LoadRecommend : CartError

    data object InvalidAuthorized : CartError

    data object Network : CartError

    data object UnKnown : CartError
}
