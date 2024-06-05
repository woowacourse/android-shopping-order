package woowacourse.shopping.data.recent

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.local.dao.RecentProductDao
import woowacourse.shopping.local.entity.RecentProductEntity.Companion.toRecentProductEntity
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
