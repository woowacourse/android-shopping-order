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

inline fun <reified T : Any?> Response<T>.checkCartError(excute: (CartError) -> Unit) = apply {
    when (this) {
        is Response.Success -> {}
        is Fail.InvalidAuthorized -> excute(CartError.InvalidAuthorized)
        is Fail.Network -> excute(CartError.Network)
        is Fail.NotFound -> {
            when (T::class) {
                Product::class -> excute(CartError.LoadRecommend)
                CartWithProduct::class -> excute(CartError.LoadCart)
                else -> excute(CartError.UnKnown)
            }
        }

        is Response.Exception -> {
            Log.d(this.javaClass.simpleName, "${this.e}")
            excute(CartError.UnKnown)
        }
    }
}

inline fun <reified T : Any?> Fail<T>.toUiError() =
    when (this) {
        is Fail.InvalidAuthorized -> CartError.InvalidAuthorized
        is Fail.Network -> CartError.Network
        is Fail.NotFound -> {
            when (T::class) {
                Product::class -> CartError.LoadRecommend
                CartWithProduct::class -> CartError.LoadCart
                else -> CartError.UnKnown
            }
        }
    }

