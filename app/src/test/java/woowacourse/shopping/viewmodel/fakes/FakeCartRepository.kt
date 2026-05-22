package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.model.PurchaseProducts

class FakeCartRepository : CartRepository {
    private val _db = mutableListOf<PurchaseProduct>()
    var shouldFail: Boolean = false

    override suspend fun insert(purchaseProduct: PurchaseProduct) {
        if (shouldFail) throw Exception("Network Error")
        val existingIndex = _db.indexOfFirst { it.product.id == purchaseProduct.product.id }
        if (existingIndex != -1) {
            val existing = _db[existingIndex]
            _db[existingIndex] = existing.copy(count = existing.count + purchaseProduct.count)
        } else {

            _db.add(purchaseProduct)
        }
    }

    override suspend fun deleteCartItem(purchaseProductId: Long) {
        if (shouldFail) throw Exception("Network Error")
        _db.removeIf { it.id == purchaseProductId }
    }

    override suspend fun updateCount(cartItemId: Long, newQuantity: Int) {
        if (shouldFail) throw Exception("Network Error")
        val index = _db.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            if (newQuantity <= 0) {
                _db.removeAt(index)
            } else {
                _db[index] = _db[index].copy(count = newQuantity)
            }
        }
    }

    override suspend fun getProductCount(): Int {
        if (shouldFail) throw Exception("Network Error")
        return _db.sumOf { it.count }
    }

    override suspend fun getPagedCart(page: Int, size: Int): PurchaseProducts {
        if (shouldFail) throw Exception("Network Error")
        val start = page * size
        if (start >= _db.size) return PurchaseProducts(emptyList())
        val end = minOf(start + size, _db.size)
        return PurchaseProducts(_db.subList(start, end))
    }

    override suspend fun getCartItemCount(): Int {
        if (shouldFail) throw Exception("Network Error")
        return _db.size
    }
}
