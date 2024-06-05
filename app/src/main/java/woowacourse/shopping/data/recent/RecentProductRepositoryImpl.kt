package woowacourse.shopping.data.recent

import woowacourse.shopping.data.recent.RecentProductEntity.Companion.toRecentProductEntity
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.RecentRepository
import java.time.LocalDateTime
import kotlin.concurrent.thread

class RecentProductRepositoryImpl(private val dao: RecentProductDao) : RecentRepository {
    override fun loadAll(): Result<List<Product>> {
        var result: Result<List<Product>>? = null
        thread {
            result =
                runCatching {
                    val recentViewed = dao.loadAll()
                    recentViewed.map { entity ->
                        entity.toDomain()
                    }
                }
        }.join()
        return result ?: throw NoSuchElementException()
    }

    override fun loadMostRecent(): Result<Product?> {
        var result: Result<Product?>? = null
        thread {
            result =
                runCatching {
                    val entity = dao.getMostRecent()
                    entity?.toDomain()
                }
        }.join()
        return result ?: throw NoSuchElementException()
    }

    override fun add(recentProduct: Product): Result<Long> {
        return runCatching {
            dao.insert(
                recentProduct.toRecentProductEntity(LocalDateTime.now()),
            )
        }
    }
}
