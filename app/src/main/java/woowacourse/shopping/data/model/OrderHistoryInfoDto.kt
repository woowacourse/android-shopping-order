package woowacourse.shopping.data.model

import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderHistoryProduct
import com.example.domain.model.OrderState
import com.example.domain.model.Price
import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryInfoDto(
    val contents: List<Content>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Int
)

@Serializable
data class Content(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val orderStatus: String,
    val productName: String,
    val productImageUrl: String,
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
