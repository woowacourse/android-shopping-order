package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.datasource.RemoteProductDataSource
import woowacourse.shopping.data.local.database.RecentProductDao
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.toCartData
import woowacourse.shopping.data.model.toCartDomain
import woowacourse.shopping.data.model.toOrderableProduct
import woowacourse.shopping.data.model.toProductDomain2
import woowacourse.shopping.data.model.toProductItemDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductDomain2
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductDataSource: RemoteProductDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
    private val recentProductDao: RecentProductDao,
) : ProductRepository {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String
    ): Result<ProductDomain2> {
        return runCatching {
            remoteProductDataSource.getProducts(category, page, size, sort)
                .toProductDomain2(getEntireCartItems())
        }
    }

    override suspend fun getProductById(id: Int): Result<OrderableProduct> {
        return runCatching {
            val cartItem = getEntireCartItems().firstOrNull { it.productId == id }
            remoteProductDataSource.getProductById(id).toOrderableProduct(cartItem)
        }
    }

    override suspend fun getRecommendedProducts(): Result<List<OrderableProduct>> {
        return runCatching {
            val lastlyViewedProduct = recentProductDao.findMostRecentProduct()
            val cartData = getEntireCartItems()
            val orderableProducts = mutableListOf<OrderableProduct>()
            var page = 0
            do {
                val products = remoteProductDataSource.getProducts(
                    category = lastlyViewedProduct?.category,
                    page = page++,
                    size = RECOMMEND_PAGE_SIZE,
                    sort = SORT_CART_ITEMS
                ).toProductDomain2(cartData).orderableProducts.filter {
                    it.cartData == null
                }
                products.forEach {
                    if (orderableProducts.size < 10) {
                        orderableProducts.add(it)
                    } else return@forEach
                }
            } while (orderableProducts.size >= 10 || products.isEmpty())
            orderableProducts
        }
    }

    private suspend fun getEntireCartItems(): List<CartData> {
        val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
        return remoteCartDataSource.getCartItems(
            PAGE_CART_ITEMS,
            totalCartQuantity,
            SORT_CART_ITEMS
        ).cartItems.map(CartItem::toCartData)
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val RECOMMEND_PAGE_SIZE = 10
        private const val SORT_CART_ITEMS = "asc"
    }
    //    override fun getProducts(
//        category: String?,
//        page: Int,
//        size: Int,
//        sort: String,
//        onSuccess: (ProductDomain) -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteProductDataSource.getProducts(category, page, size, sort).enqueue(
//            object : Callback<ProductResponse> {
//                override fun onResponse(
//                    call: Call<ProductResponse>,
//                    response: Response<ProductResponse>,
//                ) {
//                    onSuccess(response.body()?.toProductDomain() ?: throw HttpException(response))
//                }
//
//                override fun onFailure(
//                    call: Call<ProductResponse>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response = remoteProductDataSource.getProducts(category, page, size, sort).execute()
////                response.body()?.toProductDomain() ?: throw HttpException(response)
////            }.onSuccess(onSuccess).onFailure(onFailure)
////        }
//    }
//
//    override fun getProductById(
//        id: Int,
//        onSuccess: (ProductItemDomain) -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteProductDataSource.getProductById(id).enqueue(
//            object : Callback<Product> {
//                override fun onResponse(
//                    call: Call<Product>,
//                    response: Response<Product>,
//                ) {
//                    onSuccess(response.body()?.toProductItemDomain() ?: throw HttpException(response))
//                }
//
//                override fun onFailure(
//                    call: Call<Product>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response = remoteProductDataSource.getProductById(id).execute()
////
////            }.onSuccess(onSuccess).onFailure(onFailure)
////        }
//    }
}
