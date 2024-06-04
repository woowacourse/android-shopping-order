package woowacourse.shopping.data.datasource.local.dummy

import woowacourse.shopping.data.datasource.remote.model.response.Sort
import woowacourse.shopping.data.datasource.remote.model.response.product.Pageable
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductContent
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductResponse

val dummyFoodProductContents =
    List(55) {
        ProductContent(
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
        dummyFoodProductContents.subList(0, 20),
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
