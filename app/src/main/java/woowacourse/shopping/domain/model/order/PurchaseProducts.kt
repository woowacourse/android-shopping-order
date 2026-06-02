package woowacourse.shopping.domain.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PurchaseProducts(
    val purchaseProducts: List<PurchaseProduct> = emptyList(),
) : Parcelable {
    fun add(purchaseProduct: PurchaseProduct) =
        if (findByProductId(purchaseProduct.productId) == null) {
            PurchaseProducts(purchaseProducts + purchaseProduct)
        } else {
            updateCountWithUuid(purchaseProduct.productId, purchaseProduct.count)
        }

    fun updateCountWithUuid(
        id: Long,
        updateAmount: Int,
    ) = PurchaseProducts(
        purchaseProducts.map {
            if (it.isSameProductID(id)) it.updateCount(updateAmount) else it
        },
    )

    fun removeProduct(id: Long): PurchaseProducts {
        val targetPurchaseProduct = findByProductId(id) ?: return this
        return PurchaseProducts(purchaseProducts - targetPurchaseProduct)
    }

    fun totalPriceOfSpecificPurchaseProduct(id: Long): Int {
        val targetProduct = findByProductId(id) ?: return 0
        return targetProduct.totalPrice
    }

    fun totalCountOfSpecificPurchaseProduct(id: Long): Int {
        val targetProduct = findByProductId(id) ?: return 0
        return targetProduct.count
    }

    fun totalCount() = purchaseProducts.sumOf { it.count }

    fun isContain(id: Long): Boolean = purchaseProducts.any { it.productId == id }

    fun findPurchaseProductById(id: Long): PurchaseProduct? = purchaseProducts.find { it.id == id }

    fun findByProductId(id: Long) = purchaseProducts.find { it.isSameProductID(id) }
}
