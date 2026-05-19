package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductTitle
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.data.remote.retrofit.dto.ProductDto

fun ProductDto.toDomainProduct(): Product =
    Product(
        id = id,
        title = ProductTitle(name),
        price = Price(price),
        imageUrl = imageUrl,
        category = category,
    )

fun ProductResponse.toDomainProducts(): List<Product> = content.map { product -> product.toDomainProduct() }

fun Product.toShoppingItem(quantity: Int = 0): ShoppingItem =
    ShoppingItem(
        product = this,
        quantity = quantity,
    )
