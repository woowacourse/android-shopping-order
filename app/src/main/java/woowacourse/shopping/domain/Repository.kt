package woowacourse.shopping.domain

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse

interface Repository {
    fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    fun getProducts(
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>?>

    fun getCartItems(
        page: Int, size: Int
    ): Result<List<CartProduct>?>

    fun getProductById(
        id: Int
    ): Result<CartProduct?>

    fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    fun findByLimit(limit: Int): Result<List<RecentProduct>>

    fun findOne(): Result<RecentProduct?>

    fun findProductById(id: Long): Result<CartProduct?>

    fun saveCart(cart: Cart): Result<Long>

    fun saveRecent(recent: Recent): Result<Long>

    fun deleteCart(id: Long): Result<Long>

    fun getMaxCartCount(): Result<Int>
}
