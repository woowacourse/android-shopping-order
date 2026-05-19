package woowacourse.shopping.mockup

import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

fun createProduct(
    id: String,
    price: Long = 2000,
    category: String = "book",
): Product =
    Product(
        id = id,
        name = ProductName("product$id"),
        price = Money(price),
        imageUrl = "image$id",
        category = category,
    )

fun createProducts(size: Int): List<Product> = (1..size).map { createProduct(id = it.toString()) }

fun createCartItem(
    id: String,
    product: Product = createProduct(id),
    quantity: Int = 1,
): CartItem =
    CartItem(
        id = id,
        product = product,
        quantity = quantity,
    )

fun createCartItems(size: Int): List<CartItem> = (1..size).map { createCartItem(id = it.toString()) }

fun Product.toRecentItemEntity(timestamp: Long): RecentItemEntity =
    RecentItemEntity(
        id = id,
        timestamp = timestamp,
    )
