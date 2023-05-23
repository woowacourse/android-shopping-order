package woowacourse.shopping

enum class ServerType(val intentKey: String) {
    SUBAN_MOCK_SERVER("subanMockServer"),
    MINT_SERVER("mintServer"),
    JOY_SERVER("joyServer");

    companion object {
        fun getServerTypeByIntentKey(intentKey: String) =
            when (intentKey) {
                "subanMockServer" -> SUBAN_MOCK_SERVER
                "mintServer" -> MINT_SERVER
                "joyServer" -> JOY_SERVER
                else -> IllegalArgumentException("$intentKey 잘못된 intentKey 입니다.")
            }
    }
}
