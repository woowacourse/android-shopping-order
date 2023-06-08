package woowacourse.shopping.data.repository.point

import com.example.domain.model.PointInfo
import com.example.domain.repository.PointRepository
import woowacourse.shopping.data.datasource.remote.point.PointDataSourceImpl

class PointRemoteRepositoryImpl(
    private val service: PointDataSourceImpl
) : PointRepository {

    override fun getPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    ) {
        service.loadPoint(
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }
}
