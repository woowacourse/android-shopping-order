package woowacourse.shopping.mapper

import woowacourse.shopping.backend.retrofit.dto.Pageable
import woowacourse.shopping.backend.retrofit.dto.ProductResponse
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.backend.retrofit.dto.Product as ApiProduct

fun ApiProduct.toDomainProduct(): Product =
    Product(
        id = id,
        title = ProductTitle(name),
        price = Price(price),
        imageUrl = imageUrl,
    )

fun Pageable.toDomainProduct(): Product =
    Product(
        id = id,
        title = ProductTitle(name),
        price = Price(price),
        imageUrl = imageUrl,
    )

fun ProductResponse.toDomainProducts(): List<Product> = content.map { product -> product.toDomainProduct() }

fun Product.toApiProduct(category: String = DEFAULT_CATEGORY): ApiProduct =
    ApiProduct(
        category = category,
        id = id,
        imageUrl = imageUrl,
        name = getTitle(),
        price = getPrice(),
    )

fun Product.toShoppingItem(quantity: Int = 0): ShoppingItem =
    ShoppingItem(
        product = this,
        quantity = quantity,
    )

private const val DEFAULT_CATEGORY = "UNKNOWN"
