package woowacourse.shopping.data.product.remote.retrofit

import woowacourse.shopping.domain.model.PagingProduct
import woowacourse.shopping.domain.model.Product

fun Content.toProduct(): Product = Product(id, name, price, imageUrl, category)

fun List<Content>.toProduct(): List<Product> = map { it.toProduct() }

fun ProductResponse.toPagingProduct(): PagingProduct =
    PagingProduct(
        products = content.toProduct(),
        first = first,
        last = last,
        empty = empty,
    )
