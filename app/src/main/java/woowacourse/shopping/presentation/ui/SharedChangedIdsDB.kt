package woowacourse.shopping.presentation.ui

object SharedChangedIdsDB {
    private var changedProductIds: Set<Long> = emptySet()

    fun existChangedProducts(): Boolean = changedProductIds.isNotEmpty()

    fun getChangedProductsIds(): Set<Long> = changedProductIds

    fun addChangedProductsId(newIds: Set<Long>) {
        changedProductIds += newIds
    }

    fun clearChangedProductsId() {
        changedProductIds = emptySet()
    }
}
