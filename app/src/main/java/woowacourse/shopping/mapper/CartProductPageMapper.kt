package woowacourse.shopping.mapper

import woowacourse.shopping.model.CartProductPage
import woowacourse.shopping.model.CartProductPageUIModel
import woowacourse.shopping.model.PageUIModel

fun CartProductPage.toUIModel(): CartProductPageUIModel {
    return CartProductPageUIModel(
        cartProducts = cartProducts.map { it.toUIModel() },
        pageUIModel = PageUIModel(
            pagePrev = firstPage,
            pageNext = lastPage,
            pageNumber = currentPage
        )
    )
}
