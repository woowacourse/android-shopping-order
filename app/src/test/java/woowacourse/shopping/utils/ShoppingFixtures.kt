package woowacourse.shopping.utils

import org.junit.runner.manipulation.Orderable
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.model.RecentProduct
import java.time.LocalDateTime

fun getFixtureCartItems(count: Int): List<CartItemDomain> =
    List(count) {
        CartItemDomain(it + 1, it + 1, getFixtureProduct(it + 1, 1000 * (it + 1)))
    }

fun getFixtureProducts(count: Int): List<ProductItemDomain> =
    List(count) { getFixtureProduct(it + 1, 1000 * (it + 1)) }

fun getFixtureProduct(id: Int, price: Int): ProductItemDomain =
    ProductItemDomain(
        category = "fashion",
        id = id,
        imageUrl = "image",
        name = "apple${id + 1}",
        price = price
    )

fun getFixtureRecentProducts(count: Int): List<RecentProduct> =
    List(count) {
        RecentProduct(
            productId = it + 1,
            productName = "apple${it + 1}",
            imageUrl = "image",
            dateTime = LocalDateTime.now(),
            category = "fashion"
        )
    }

fun getFixtureOrderableProducts(count: Int): List<OrderableProduct> =
    getFixtureProducts(100).map {
        OrderableProduct(it, null)
    }
