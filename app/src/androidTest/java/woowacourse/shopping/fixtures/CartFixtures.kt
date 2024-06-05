package woowacourse.shopping.fixtures

import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.local.entity.CartEntity

private const val DEFAULT_ID = 1L
private const val DEFAULT_COUNT = 0

fun fakeCartEntity(
    id: Long = DEFAULT_ID,
    count: Int = DEFAULT_COUNT,
): CartEntity {
    return CartEntity(id, count)
}

fun fakeCartEntities(vararg ids: Long): List<CartEntity> {
    return ids.map { fakeCartEntity(it) }
}

fun fakeCartProduct(
    productId: Long = 1L,
    name: String = "오둥이 $productId",
    price: Int = 1000,
    count: Int = 1,
) = CartProduct(
    fakeProduct(productId, name = name, price = price),
    count,
)

fun fakeCartProducts(size: Int): List<CartProduct> {
    return List(size) {
        fakeCartProduct(productId = (it + 1).toLong(), name = "오둥이 ${it + 1}")
    }
}

fun fakeCartProducts(vararg products: Product): List<CartProduct> {
    return products.map { fakeCartProduct(it.id) }
}

fun fakeCartProducts(products: List<Product>): List<CartProduct> {
    return products.map { fakeCartProduct(it.id, it.name) }
}
