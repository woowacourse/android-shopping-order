package woowacourse.shopping.data

import com.example.domain.model.PointInfo
import com.example.domain.repository.PointRepository
import woowacourse.shopping.data.service.PointRemoteService

class PointRemoteRepositoryImpl(
    private val service: PointRemoteService
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
