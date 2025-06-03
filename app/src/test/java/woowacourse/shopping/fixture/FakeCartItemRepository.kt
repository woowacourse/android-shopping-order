package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class FakeCartItemRepository(
    private val size: Int,
) : CartItemRepository {
    private val fakeCartItems = mutableListOf<ProductUiModel>(
        *List(size) { index ->
            ProductUiModel(
                id = (index + 1).toLong(),
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
                quantity = (index + 1),
            )
        }.toTypedArray()
    )

    override fun getInitialCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<List<CachedCartItem>>) -> Unit,
    ) {
        val cachedItems = fakeCartItems.map { product ->
            CachedCartItem(
                cartId = product.id,
                productId = product.id,
                quantity = product.quantity
            )
        }

        onResult(Result.success(cachedItems))
    }

    override fun getCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<PagingData>) -> Unit,
    ) {
        if (page != null && size != null) {
            val startIndex = page * size
            val endIndex = (startIndex + size).coerceAtMost(fakeCartItems.size)

            val pageItems = if (startIndex < fakeCartItems.size) {
                fakeCartItems.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

            val pagingData = PagingData(
                products = pageItems,
                page = page,
                hasNext = endIndex < fakeCartItems.size,
                hasPrevious = page > 0
            )

            onResult(Result.success(pagingData))
        }
    }

    override fun deleteCartItem(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        fakeCartItems.removeIf { it.id == id }
        onResult(Result.success(Unit))
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val existing = fakeCartItems.find { it.id == id }
        if (existing == null) {
            fakeCartItems.add(
                ProductUiModel(
                    id = id,
                    name = "$id 아이스 카페 아메리카노",
                    imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                    price = 1000 * id.toInt(),
                    quantity = quantity
                )
            )
        }
        onResult(Result.success(Unit))
    }

    override fun updateCartItemQuantity(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val index = fakeCartItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = fakeCartItems[index]
            val updated = existing.copy(quantity = quantity)
            fakeCartItems[index] = updated
        }
        onResult(Result.success(Unit))
    }


    override fun addCartItemQuantity(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val index = fakeCartItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = fakeCartItems[index]
            val updated = existing.copy(quantity = existing.quantity + quantity)
            fakeCartItems[index] = updated
        } else {
            fakeCartItems.add(
                ProductUiModel(
                    id = id,
                    name = "$id 아이스 카페 아메리카노",
                    imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                    price = 1000 * id.toInt(),
                    quantity = quantity
                )
            )
        }
        onResult(Result.success(Unit))
    }

    override fun getCartItemsCount(onResult: (Result<Int>) -> Unit) {
        val totalCount = fakeCartItems.sumOf { it.quantity }
        onResult(Result.success(totalCount))
    }

    override fun getQuantity(pagingData: PagingData): PagingData {
        val updatedProducts = pagingData.products.map { product ->
            val cartItem = fakeCartItems.find { it.id == product.id }
            product.copy(quantity = cartItem?.quantity ?: 0)
        }

        return pagingData.copy(products = updatedProducts)
    }

    override fun getCartItemProductIds(): List<Long> {
        return fakeCartItems.map { it.id }
    }

    override fun getCartItemCartIds(): List<Long> {
        return fakeCartItems.map { it.id }
    }
}