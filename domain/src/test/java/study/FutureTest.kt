package study

import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class FutureTest {
    @Test
    internal fun name() {
        printThreadId(" 메인 스레드")
        CompletableFuture.supplyAsync {
            repositoryGet()
        }.thenAccept {
            printThreadId("만들어진 작업 결과 받아서 처리하는 중")
        }

        CompletableFuture.completedFuture("test").thenAccept {
            printThreadId("thenAccept 가짜 작업 결과 받고 콜백 실행중")
        }
    }

    fun repositoryGet(): String {
        return "가짜 데이터"
    }

    private fun printThreadId(message: String) {
        println("${Thread.currentThread().id}, $message")
    }
}
