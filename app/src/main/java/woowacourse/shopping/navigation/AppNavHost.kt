package woowacourse.shopping.navigation

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object ShoppingList

@Serializable
data class ProductDetail(
    val productId: Long,
)

@Composable
fun AppNavHost() {
}
