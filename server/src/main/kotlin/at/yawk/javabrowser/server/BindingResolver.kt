package at.yawk.javabrowser.server

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import java.net.URI
import java.net.URLEncoder

/**
 * @author yawkat
 */
class BindingResolver(private val dbi: DBI) {
    private val cache: LoadingCache<String, List<BindingLocation>> = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .build(CacheLoader.from { binding ->
                dbi.inTransaction { conn: Handle, _ ->
                    val candidates = conn.select("select artifactId, sourceFile from bindings join artifacts a on bindings.artifactId = a.id where a.lastCompileVersion = ? and binding = ?",
                            Compiler.VERSION,
                            binding)

                    candidates.map { BindingLocation(it["artifactId"] as String, it["sourceFile"] as String, binding!!) }
                }
            })

    fun invalidate() {
        cache.invalidateAll()
    }

    fun resolveBinding(fromArtifact: String, binding: String): List<URI> {
        val candidates = cache[binding]
        for (candidate in candidates) {
            if (candidate.artifactId == fromArtifact) {
                return listOf(candidate.uri)
            }
        }
        return candidates.map { it.uri }
    }

    private data class BindingLocation(
            val artifactId: String,
            val sourceFile: String,
            val binding: String
    ) {
        val uri = URI.create("/$artifactId/$sourceFile#${URLEncoder.encode(binding, "UTF-8")}")!!
    }
}