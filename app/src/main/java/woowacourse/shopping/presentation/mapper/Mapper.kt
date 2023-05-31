package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.OrderProduct
import woowacourse.shopping.OrderProducts
import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.presentation.model.ProductModel

fun Product.toPresentation(): ProductModel {
    return ProductModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price.value,
    )
}

fun ProductModel.toDomain(): Product {
    return Product(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = Price(price),
    )
}

fun CartProductInfo.toPresentation(): CartProductInfoModel {
    return CartProductInfoModel(
        id = id,
        productModel = product.toPresentation(),
        count = count,
        isOrdered = isOrdered,
    )
}

fun CartProductInfoModel.toDomain(): CartProductInfo {
    return CartProductInfo(
        id = id,
        product = productModel.toDomain(),
        count = count,
        isOrdered = isOrdered,
    )
}

fun OrderProductModel.toDomain(): OrderProduct {
    return OrderProduct(
        product = product.toDomain(),
        count = count,
    )
}

fun OrderProductsModel.toDomain(): OrderProducts {
    return OrderProducts(
        list.map { it.toDomain() }
    )
}
