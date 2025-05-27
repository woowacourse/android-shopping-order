package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CartProductDetailResponse
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

fun CartProductDetailResponse.toDomain(): CartProduct =
    CartProduct(
        productDetail = product?.toDomain() ?: EMPTY_PRODUCT_DETAIL,
        quantity = cartProduct.quantity,
    )
