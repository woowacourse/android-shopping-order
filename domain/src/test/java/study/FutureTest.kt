package study

import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class FutureTest {
    @Test
    internal fun name() {
        println("\n\n\n\n")
        printThreadId(" 메인 스레드")
        val completableFuture: CompletableFuture<String> = CompletableFuture.supplyAsync {
            printThreadId("작업 결과 만드는 중")
            "작업 결과"
        }

        completableFuture.thenAccept {
            printThreadId("다른 스레드로 작업 결과 만드는 중")
        }

        CompletableFuture.completedFuture("test").thenAccept {
            printThreadId("thenAccept 가짜 작업 결과 받고 콜백 실행중")
        }
    }

    private fun printThreadId(message: String) {
        println("${Thread.currentThread().id}, $message")
    }
}
