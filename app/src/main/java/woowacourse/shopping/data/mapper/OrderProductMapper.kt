package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.OrderRequestDto
import woowacourse.shopping.data.dto.OrderResponseDto
import woowacourse.shopping.data.dto.OrderedProductDto
import woowacourse.shopping.data.dto.PaymentDto
import woowacourse.shopping.data.model.DataOrderedProduct
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.OrderedProduct
import woowacourse.shopping.domain.model.Payment

fun OrderRequestDto.toDomain(): OrderRequest =
    OrderRequest(
        orderItems = orderItems,
        payment = payment.toDomain(),
    )

fun OrderRequest.toDto(): OrderRequestDto =
    OrderRequestDto(
        orderItems = orderItems,
        payment = payment.toDto(),
    )

fun OrderResponseDto.toDomain(): OrderResponse =
    OrderResponse(
        orderId = orderId,
        orderedProducts = orderedProducts.toDomain(),
        payment = payment.toDomain(),
    )

fun OrderResponse.toDto(): OrderResponseDto =
    OrderResponseDto(
        orderId = orderId,
        orderedProducts = orderedProducts.toDto(),
        payment = payment.toDto(),
    )

fun OrderedProductDto.toDomain(): OrderedProduct =
    OrderedProduct(
        name = name,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
    )

fun OrderedProduct.toDto(): OrderedProductDto =
    OrderedProductDto(
        name = name,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
    )

fun OrderedProduct.toUiModel(): DataOrderedProduct =
    DataOrderedProduct(
        name = name,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
    )

fun PaymentDto.toDomain(): Payment =
    Payment(
        originalPayment = originalPayment,
        finalPayment = finalPayment,
        point = point,
    )

fun Payment.toDto(): PaymentDto =
    PaymentDto(
        originalPayment = originalPayment,
        finalPayment = finalPayment,
        point = point,
    )
