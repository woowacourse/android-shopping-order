package woowacourse.shopping.data.model

import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderHistoryProduct
import com.example.domain.model.OrderState
import com.example.domain.model.Price

data class OrderHistoryInfoDto(
    val contents: List<Content>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class Content(
    val orderAt: String,
    val orderId: Int,
    val orderStatus: String,
    val payAmount: Int,
    val productImageUrl: String,
    val productName: String,
    val totalPrice: Int,
    val totalProductCount: Int
)

private fun Content.toDomain() = OrderHistoryProduct(
    orderId,
    Price(payAmount),
    orderAt,
    orderStatus.toDomain(),
    productName,
    productImageUrl,
    totalProductCount
)

fun OrderHistoryInfoDto.toDomain() =
    OrderHistoryInfo(totalPages, currentPage, pageSize, contents.map { it.toDomain() })

fun String.toDomain() = OrderState.values().first { it.value == this }
