package at.yawk.javabrowser.server

import org.postgresql.PGConnection
import org.skife.jdbi.v2.DBI
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author yawkat
 */
class ArtifactUpdater(dbi: DBI) {
    companion object {
        private val log = LoggerFactory.getLogger(ArtifactUpdater::class.java)
    }

    private val artifactListeners = ArrayList<(String) -> Unit>()
    private val invalidationListeners = ArrayList<() -> Unit>()

    private val pool: Executor

    init {
        var i = 0
        pool = Executors.newCachedThreadPool { Thread(it, "Artifact updater #${++i}") }
    }

    @Synchronized
    fun addArtifactUpdateListener(listener: (String) -> Unit) {
        artifactListeners.add(listener)
    }

    @Synchronized
    fun addInvalidationListener(runAtStart: Boolean = false, listener: () -> Unit) {
        invalidationListeners.add(listener)
        if (runAtStart) {
            pool.execute(listener)
        }
    }

    @Synchronized
    private fun onUpdate(artifactId: String) {
        for (listener in artifactListeners) {
            pool.execute { listener(artifactId) }
        }
    }

    @Synchronized
    private fun onInvalidate() {
        for (listener in invalidationListeners) {
            pool.execute(listener)
        }
    }

    init {
        Thread({
            while (true) {
                try {
                    dbi.useHandle {
                        it.update("listen artifacts")

                        while (true) {
                            val notifications = it.connection.unwrap(PGConnection::class.java).getNotifications(0)
                            var invalidate = false
                            for (notification in notifications) {
                                if (notification.name != "artifacts") {
                                    log.error("Received notification of name we did not listen to: {}",
                                            notification.name)
                                    continue
                                }

                                onUpdate(notification.parameter)
                                invalidate = true
                            }
                            if (invalidate) {
                                onInvalidate()
                            }
                        }
                    }
                } catch (e: Exception) {
                    log.error("Error during artifact update wait", e)
                }
            }
        }, "Artifact update thread").start()
    }
}