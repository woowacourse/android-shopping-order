package woowacourse.shopping.ui.products.uimodel

import android.util.Log
import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response

sealed interface ProductListError {
    data object LoadRecentProduct : ProductListError
    data object LoadProduct : ProductListError

    data object AddCart : ProductListError

    data object UpdateCount : ProductListError

    data object InvalidAuthorized : ProductListError

    data object Network : ProductListError

    data object UnKnown : ProductListError

}

inline fun <reified T : Any?> Response<T>.checkError(excute: (ProductListError) -> Unit) = apply {
    when (this) {
        is Response.Success -> {}
        is Fail.InvalidAuthorized -> excute(ProductListError.InvalidAuthorized)
        is Fail.Network -> excute(ProductListError.Network)
        is Fail.NotFound -> {
            when (T::class) {
                Product::class -> excute(ProductListError.LoadProduct)
                RecentProduct::class -> excute(ProductListError.LoadRecentProduct)
                else -> excute(ProductListError.UnKnown)
            }
        }

        is Response.Exception -> {
            Log.d(this.javaClass.simpleName, "${this.e}")
            excute(ProductListError.UnKnown)
        }
    }
}
