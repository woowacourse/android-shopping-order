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
