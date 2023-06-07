package woowacourse.shopping.data.remote.dto

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.ProductsWithCartItem

fun ProductDTO.toDomain(): Product {
    if (!isNotNull) throw IllegalArgumentException()
    return Product(id ?: -1, name ?: "", Price(price ?: 0), imageUrl ?: "")
}

fun CartProductDTO.toDomain(): CartProduct {
    if (!isNotNull) throw IllegalArgumentException()
    return CartProduct(
        id ?: -1,
        quantity ?: 0,
        product?.toDomain() ?: Product(-1, "", Price(0), ""),
    )
}

fun OrdersDTO.OrderDTO.toDomain(): Order {
    if (!isNotNull) throw IllegalArgumentException()
    return Order(
        orderId ?: -1,
        orderedDateTime ?: "",
        products?.map { it.toDomain() }
            ?: emptyList(),
        totalPrice ?: -1,
    )
}

fun ProductWithQuantityDTO.toDomain(): ProductWithQuantity {
    if (!isNotNull) throw IllegalArgumentException()
    return ProductWithQuantity(product?.toDomain() ?: Product(-1, "", Price(0), ""), quantity ?: -1)
}

fun OrdersDTO.toDomain(): List<Order> {
    if (!isNotNull) throw IllegalArgumentException()
    return orders?.map {
        it.toDomain()
    } ?: emptyList()
}

fun ProductWithCartInfoDTO.toDomain(): ProductWithCartInfo {
    if (!isNotNull) throw IllegalArgumentException()
    return ProductWithCartInfo(
        product?.toDomain() ?: Product(-1, "", Price(0), ""),
        ProductWithCartInfo.CartItem(
            cartItem?.id ?: -1,
            cartItem?.quantity ?: 0,
        ),
    )
}

fun ProductsWithCartItemDTO.toDomain(): ProductsWithCartItem {
    if (!isNotNull) throw IllegalArgumentException()
    return ProductsWithCartItem(
        products?.map {
            it?.toDomain() ?: ProductWithCartInfo(
                Product(-1, "", Price(0), ""),
                null,
            )
        } ?: emptyList(),
        last ?: true,
    )
}
