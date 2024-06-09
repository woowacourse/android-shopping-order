package woowacourse.shopping.ui.detail.uimodel

import android.util.Log
import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartError

sealed interface ProductDetailError {
    data object LoadProduct:ProductDetailError

    data object ChangeCount:ProductDetailError

    data object AddCart:ProductDetailError


    data object InvalidAuthorized : ProductDetailError

    data object Network : ProductDetailError

    data object UnKnown : ProductDetailError
}

inline fun <reified T : Any?> Response<T>.checkProductDetailError(execute: (ProductDetailError) -> Unit) = apply {
    when (this) {
        is Response.Success -> {}
        is Fail.InvalidAuthorized -> execute(ProductDetailError.InvalidAuthorized)
        is Fail.Network -> execute(ProductDetailError.Network)
        is Fail.NotFound -> {
            when (T::class) {
                Product::class -> execute(ProductDetailError.LoadProduct)
                CartWithProduct::class -> execute(ProductDetailError.AddCart)
                else -> execute(ProductDetailError.UnKnown)
            }
        }

        is Response.Exception -> {
            Log.d(this.javaClass.simpleName, "${this.e}")
            execute(ProductDetailError.UnKnown)
        }
    }
}

inline fun <reified T : Any?> Fail<T>.toProductDetailUiError() =
    when (this) {
        is Fail.InvalidAuthorized -> ProductDetailError.InvalidAuthorized
        is Fail.Network -> ProductDetailError.Network
        is Fail.NotFound -> {
            when (T::class) {
                Product::class -> ProductDetailError.LoadProduct
                else -> ProductDetailError.UnKnown
            }
        }
    }
