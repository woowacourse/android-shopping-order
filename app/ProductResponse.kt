
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val category: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int
)
