package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PurchaseProducts(
    val purchaseProducts: List<PurchaseProduct> = emptyList(),
) : Parcelable {
    fun add(purchaseProduct: PurchaseProduct) =
        if (findById(purchaseProduct.productId()) == null) {
            PurchaseProducts(purchaseProducts + purchaseProduct)
        } else {
            updateCountWithUuid(purchaseProduct.productId(), purchaseProduct.count)
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
        val targetPurchaseProduct = findById(id) ?: return this
        return PurchaseProducts(purchaseProducts - targetPurchaseProduct)
    }

    fun totalPriceOfSpecificPurchaseProduct(id: Long): Int {
        val targetProduct = findById(id) ?: return 0
        return targetProduct.totalPrice()
    }

    fun totalCountOfSpecificPurchaseProduct(id: Long): Int {
        val targetProduct = findById(id) ?: return 0
        return targetProduct.count
    }

    fun totalCount() = purchaseProducts.sumOf { it.count }

    fun isContain(id: Long): Boolean = purchaseProducts.any { it.productId() == id }

    fun findPurchaseProductById(id: Long): PurchaseProduct? = purchaseProducts.find { it.id == id }

    fun findById(id: Long) = purchaseProducts.find { it.isSameProductID(id) }
}
