package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.data.model.response.CartProductDetailResponse
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

fun CartProductDetailResponse.toDomain(): Product =
    Product(
        productDetail = product?.toDomain() ?: EMPTY_PRODUCT_DETAIL,
        quantity = cartProduct.quantity,
    )

fun CartItemsResponse.Content.toDomain(): Product =
    Product(
        productDetail =
            ProductDetail(
                id = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
                category = product.category,
            ),
        cartId = id,
        quantity = quantity,
    )
