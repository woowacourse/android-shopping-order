package woowacourse.shopping.fixture

import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

fun MockWebServer.allRequests(): List<RecordedRequest> =
    buildList {
        repeat(requestCount) {
            add(takeRequest())
        }
    }
