package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.ProductModel
import woowacouse.shopping.model.product.Product

fun Product.toUIModel(): ProductModel = ProductModel(id, title, price, imageUrl)

fun ProductModel.toModel(): Product = Product(id, title, price, imageUrl)
