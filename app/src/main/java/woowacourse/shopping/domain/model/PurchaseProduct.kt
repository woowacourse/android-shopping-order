package woowacourse.shopping.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchaseProduct(
    val id: Long,
    val product: Product,
    val count: Int = 1,
) : Parcelable {

    val name: String
        get() = product.name

    val price: Int
        get() = product.price

    val imageUri: String
        get() = product.imageUri

    val productId: Long
        get() = product.id

    val totalPrice: Int
        get() = product.price * count

    init {
        require(count > 0) { "구매할 상품의 개수는 1개 이상이어야 합니다." }
    }

    fun updateCount(updateAmount: Int): PurchaseProduct {
        val newCount = count + updateAmount
        return copy(count = newCount)
    }

    fun isSameProductID(id: Long) = id == product.id
}
