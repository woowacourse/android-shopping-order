package woowacourse.shopping.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
object ProductList

@Serializable
data class ProductDetail(
    val productId: Long,
)

@Serializable
object Cart

@Serializable
object Settings

@Serializable
object CartGraph

@Serializable
data class OrderProduct(
    val productId: Long,
    val quantity: Int,
    val price: Int,
)

val OrderProductListType =
    object : NavType<List<OrderProduct>>(isNullableAllowed = false) {
        override fun put(
            bundle: Bundle,
            key: String,
            value: List<OrderProduct>,
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun get(
            bundle: Bundle,
            key: String,
        ): List<OrderProduct>? = bundle.getString(key)?.let { Json.decodeFromString(it) }

        override fun parseValue(value: String): List<OrderProduct> = Json.decodeFromString(Uri.decode(value))

        override fun serializeAsValue(value: List<OrderProduct>): String = Uri.encode(Json.encodeToString(value))
    }

@Serializable
data class CartRecommendation(
    val orderProducts: List<OrderProduct>,
)

@Serializable
data class Payment(
    val orderProducts: List<OrderProduct> = emptyList(),
)
