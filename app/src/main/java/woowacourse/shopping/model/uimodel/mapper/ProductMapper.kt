package woowacourse.shopping.model.uimodel.mapper

import com.shopping.domain.CartProduct
import com.shopping.domain.Count
import com.shopping.domain.Order
import com.shopping.domain.OrderDetail
import com.shopping.domain.OrderProduct
import com.shopping.domain.Product
import com.shopping.domain.RecentProduct
import woowacourse.shopping.model.data.dto.CartProductDTO
import woowacourse.shopping.model.data.dto.OrderDetailDTO
import woowacourse.shopping.model.data.dto.ProductDTO
import woowacourse.shopping.model.uimodel.CartProductUIModel
import woowacourse.shopping.model.uimodel.OrderDetailUIModel
import woowacourse.shopping.model.uimodel.OrderProductUIModel
import woowacourse.shopping.model.uimodel.OrderUIModel
import woowacourse.shopping.model.uimodel.ProductUIModel
import woowacourse.shopping.model.uimodel.RecentProductUIModel

fun ProductUIModel.toDomain() =
    Product(id, name, url, price)

fun Product.toUIModel() =
    ProductUIModel(id, name, url, price)

fun RecentProductUIModel.toDomain() =
    RecentProduct(productUIModel.toDomain())

fun RecentProduct.toUIModel() =
    RecentProductUIModel(product.toUIModel())

fun CartProductUIModel.toDomain() =
    CartProduct(id, productUIModel.toDomain(), count, isSelected)

fun CartProduct.toUIModel() =
    CartProductUIModel(id, product.toUIModel(), count, isSelected)

fun List<ProductUIModel>.toDomain() = this.map { Product(it.id, it.name, it.url, it.price) }

fun List<Product>.toUIModel() = this.map { ProductUIModel(it.id, it.name, it.url, it.price) }

fun ProductDTO.toDomain(): Product = Product(id, name, imageUrl, price)

fun CartProductDTO.toDomain(): CartProduct = CartProduct(id, product.toDomain(), Count(quantity), true)

fun CartProductDTO.transformToOrderProduct(): OrderProduct =
    OrderProduct(product.name, product.imageUrl, quantity, product.price * quantity)

fun Order.toUIModel(): OrderUIModel =
    OrderUIModel(orderId, orderPrice, totalAmount, previewName)

fun OrderProductUIModel.toDomain() = OrderProduct(name, imageUrl, count, price)

fun OrderProduct.toUIModel() = OrderProductUIModel(name, imageUrl, count, price)

fun OrderDetailDTO.toUIModel() =
    OrderDetailUIModel(
        orderItems.map { OrderProductUIModel(it.name, it.imageUrl, it.count, it.price) },
        originalPrice,
        usedPoints,
        orderPrice
    )

fun OrderDetail.toUIModel(): OrderDetailUIModel =
    OrderDetailUIModel(orderItems.map { it.toUIModel() }, originalPrice, usedPoints, orderPrice)
