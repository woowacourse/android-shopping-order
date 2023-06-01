package woowacourse.shopping.data.mapper

import com.example.domain.Product
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.presentation.model.ProductModel

fun ProductEntity.toUIModel(): ProductModel = ProductModel(id, name, price, imageUrl)

fun ProductEntity.toDomain(): Product = Product(id, name, price, imageUrl)

fun ProductModel.toEntity(): ProductEntity = ProductEntity(id, title, price, imageUrl)

fun ProductModel.toDomain(): Product = Product(id, title, price, imageUrl)

fun Product.toUiModel(): ProductModel = ProductModel(id, title, price, imageUrl)
