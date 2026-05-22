package woowacourse.shopping.domain

class Cart(
    val purchaseProducts: PurchaseProducts = PurchaseProducts(),
) {
    fun add(purchaseProduct: PurchaseProduct) = Cart(purchaseProducts.add(purchaseProduct))

    fun updateCountWithId(
        id: Long,
        updateAmount: Int,
    ) = Cart(purchaseProducts.updateCountWithUuid(id, updateAmount))

    fun removeWithId(id: Long) = Cart(purchaseProducts.removeProduct(id))

    fun totalPriceOfSpecificPurchaseProduct(id: Long) = purchaseProducts.totalPriceOfSpecificPurchaseProduct(id)

    fun totalCountOfPurchaseProducts() = purchaseProducts.totalCount()

    fun totalCountOfSpecificPurchaseProduct(id: Long) = purchaseProducts.totalCountOfSpecificPurchaseProduct(id)

    fun isContain(id: Long) = purchaseProducts.isContain(id)

    fun findById(id: Long) = purchaseProducts.findById(id)
}
