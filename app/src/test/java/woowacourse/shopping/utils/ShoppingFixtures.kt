package woowacourse.shopping.utils

import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.model.RecentProduct
import java.time.LocalDateTime

fun getFixtureCartItems(
    count: Int,
    unitPrice: Int = 1000,
): List<CartItemDomain> =
    List(count) {
        CartItemDomain(it + 1, it + 1, getFixtureProduct(it + 1, unitPrice * (it + 1)))
    }

fun getFixtureProducts(count: Int): List<ProductItemDomain> = List(count) { getFixtureProduct(it + 1, 1000 * (it + 1)) }

fun getFixtureProduct(
    id: Int,
    price: Int,
): ProductItemDomain =
    ProductItemDomain(
        category = "fashion",
        id = id,
        imageUrl = "image",
        name = "apple$id",
        price = price,
    )

fun getFixtureRecentProducts(count: Int): List<RecentProduct> =
    List(count) {
        RecentProduct(
            productId = it + 1,
            productName = "apple${it + 1}",
            imageUrl = "image",
            dateTime = LocalDateTime.now(),
            category = "fashion",
        )
    }

fun getFixtureOrderableProducts(
    count: Int,
    cartDataCount: Int = 10,
): List<OrderableProduct> =
    getFixtureProducts(count).mapIndexed { index, product ->
        if (index < cartDataCount) {
            OrderableProduct(
                product,
                CartData(index + 1, productId = product.id, quantity = 1),
            )
        } else {
            OrderableProduct(product, null)
        }
    }
