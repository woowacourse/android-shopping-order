import kotlinx.serialization.Serializable

@Serializable
object ProductList

@Serializable
data class ProductDetail(
    val productId: Long,
)

@Serializable
object Cart

@Serializable
data class CartRecommendation(
    val selectedCartItemIds: LongArray,
)

@Serializable
object Payment
