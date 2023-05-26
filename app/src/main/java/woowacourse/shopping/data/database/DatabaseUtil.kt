package woowacourse.shopping.data.database

import woowacourse.shopping.data.model.Server

fun getTableName(server: Server): String =
    when (server) {
        Server.BASE_URL_JENNA -> {
            server.name.substringAfterLast("URL_").lowercase()
        }
        Server.BASE_URL_POI -> {
            server.name.substringAfterLast("URL_").lowercase()
        }
        Server.BASE_URL_TORI -> {
            server.name.substringAfterLast("URL_").lowercase()
        }
    }
