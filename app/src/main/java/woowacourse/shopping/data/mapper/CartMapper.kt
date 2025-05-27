package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.data.model.response.CartProductDetailResponse
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

fun CartProductDetailResponse.toDomain(): CartProduct =
    CartProduct(
        id = 0,
        productDetail = product?.toDomain() ?: EMPTY_PRODUCT_DETAIL,
        quantity = cartProduct.quantity,
    )

fun CartItemsResponse.Content.toDomain(): CartProduct =
    CartProduct(
        id = id,
        productDetail =
            ProductDetail(
                id = product.id.toInt(),
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
            ),
        quantity = quantity,
    )
