package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.remote.retrofit.Content
import woowacourse.shopping.data.product.remote.retrofit.Pageable
import woowacourse.shopping.data.product.remote.retrofit.ProductResponse
import woowacourse.shopping.data.product.remote.retrofit.Sort
import woowacourse.shopping.data.product.remote.retrofit.toProduct
import woowacourse.shopping.domain.model.PagingProduct

val dummyFoodContents =
    List(55) {
        Content(
            id = it,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/27/41/8412707034127_1.png",
            name = "마리오 그린올리브 300g",
            price = 4500,
            category = "food",
        )
    }

val dummyPageable =
    Pageable(
        0,
        0,
        0,
        true,
        Sort(false, false, false),
        false,
    )

val dummyProductResponse =
    ProductResponse(
        dummyFoodContents.subList(0, 20),
        false,
        false,
        false,
        0,
        20,
        dummyPageable,
        20,
        Sort(false, false, false),
        55,
        3,
    )

val dummyPagingProduct =
    PagingProduct(
        dummyFoodContents.subList(0, 20).toProduct(),
        dummyProductResponse.first,
        dummyProductResponse.last,
        dummyProductResponse.empty,
    )
