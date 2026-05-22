package woowacourse.shopping.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import woowacourse.shopping.domain.model.CartItem

@Serializable
data class OrderItem(
    val productId: Long,
    val quantity: Int,
)

fun CartItem.toParam() =
    OrderItem(
        productId = product.id,
        quantity = quantity,
    )

val OrderItemListType =
    object : NavType<List<OrderItem>>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): List<OrderItem>? = bundle.getString(key)?.let { Json.decodeFromString(it) }

        override fun parseValue(value: String): List<OrderItem> = Json.decodeFromString(value)

        override fun put(
            bundle: Bundle,
            key: String,
            value: List<OrderItem>,
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: List<OrderItem>): String = Json.encodeToString(value)
    }
