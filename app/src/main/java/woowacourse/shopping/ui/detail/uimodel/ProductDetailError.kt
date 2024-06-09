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
