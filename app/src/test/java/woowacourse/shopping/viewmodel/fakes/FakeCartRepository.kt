package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class FakeCartRepository : CartRepository {
    private val _db = mutableListOf<PurchaseProduct>()

    override suspend fun insert(purchaseProduct: PurchaseProduct): ApiResult<Unit> {
        val existingIndex = _db.indexOfFirst { it.product.id == purchaseProduct.product.id }
        if (existingIndex != -1) {
            val existing = _db[existingIndex]
            _db[existingIndex] = existing.copy(count = existing.count + purchaseProduct.count)
        } else {
            _db.add(purchaseProduct)
        }
        return ApiResult.Success(Unit)
    }

    override suspend fun deleteCartItem(purchaseProductId: Long): ApiResult<Unit> {
        _db.removeIf { it.id == purchaseProductId }
        return ApiResult.Success(Unit)
    }

    override suspend fun updateCount(
        cartItemId: Long,
        newQuantity: Int,
    ): ApiResult<Unit> {
        val index = _db.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            if (newQuantity <= 0) {
                _db.removeAt(index)
            } else {
                _db[index] = _db[index].copy(count = newQuantity)
            }
        }
        return ApiResult.Success(Unit)
    }

    override suspend fun getProductCount(): ApiResult<Int> = ApiResult.Success(_db.sumOf { it.count })

    override suspend fun getPagedCart(
        page: Int,
        size: Int,
    ): ApiResult<PurchaseProducts> {
        val start = page * size
        if (start >= _db.size) return ApiResult.Success(PurchaseProducts(emptyList()))
        val end = minOf(start + size, _db.size)
        return ApiResult.Success(PurchaseProducts(_db.subList(start, end).toList()))
    }

    override suspend fun getCartItemCount(): ApiResult<Int> = ApiResult.Success(_db.size)
}
