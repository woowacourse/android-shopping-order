package woowacourse.shopping.presentation.ui

object SharedChangedIdsDB {
    private var changedProductIds: Set<Long> = emptySet()
    private var addedFromRecommendProductIds: Set<Long> = emptySet()

    fun existChangedProducts(): Boolean = changedProductIds.isNotEmpty()

    fun getChangedProductsIds(): Set<Long> = changedProductIds

    fun addChangedProductsId(newIds: Set<Long>) {
        changedProductIds += newIds
    }

    fun clearChangedProductsId() {
        changedProductIds = emptySet()
    }

    fun existAddedRecommendProducts(): Boolean = addedFromRecommendProductIds.isNotEmpty()

    fun getAddedRecommendProductsIds(): Set<Long> = addedFromRecommendProductIds

    fun addRecommendProductsIds(newIds: Set<Long>) {
        addedFromRecommendProductIds += newIds
    }

    fun clearAddedRecommendProductsIds() {
        addedFromRecommendProductIds = emptySet()
    }
}
