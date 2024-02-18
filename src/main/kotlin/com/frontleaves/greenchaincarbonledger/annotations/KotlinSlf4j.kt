import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * KotlinSlf4j - Kotlin日志系统
 *
 * @since v1.0.0-SNAPSHOT
 * @see org.slf4j.Logger
 * @see org.slf4j.LoggerFactory
 * @author xiao_lfeng
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KotlinSlf4j {
    companion object {
        val <reified T> T.log: Logger
            inline get() = LoggerFactory.getLogger(T::class.java)
    }
}