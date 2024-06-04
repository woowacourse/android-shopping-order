package woowacourse.shopping.data.datasource.remote.model.response.product

import woowacourse.shopping.domain.model.Product

fun ProductContent.toProduct(): Product = Product(id, name, price, imageUrl, category)

fun List<ProductContent>.toProduct(): List<Product> = map { it.toProduct() }

fun ProductResponse.toProductList(): List<Product> = this.productContent.toProduct()
