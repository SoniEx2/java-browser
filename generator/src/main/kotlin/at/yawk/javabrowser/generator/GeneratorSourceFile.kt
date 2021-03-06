package at.yawk.javabrowser.generator

import at.yawk.javabrowser.BindingRef
import at.yawk.javabrowser.BindingRefType
import at.yawk.javabrowser.PositionedAnnotation
import at.yawk.javabrowser.SourceAnnotation
import at.yawk.javabrowser.Style

/**
 * @author yawkat
 */
class GeneratorSourceFile(
        val pkg: String?,
        val text: String,
        @Suppress("MemberVisibilityCanBePrivate") val entries: MutableList<PositionedAnnotation> = ArrayList()
) {
    fun annotate(start: Int, length: Int, annotation: SourceAnnotation) {
        entries.add(PositionedAnnotation(start, length, annotation))
    }

    fun bake() {
        entries.sortWith(Comparator
                .comparingInt { it: PositionedAnnotation -> it.start }
                // first 0-length items, then the longest items
                // this avoids unnecessary nesting
                .thenComparingInt { it -> if (it.length == 0) Int.MIN_VALUE else it.length.inv() })

        // try to merge entries that affect the same text
        var i = 0
        while (i < entries.size) {
            val head = entries[i]
            var j = i + 1
            var merged: SourceAnnotation? = null
            while (merged == null && j < entries.size &&
                    entries[j].start == head.start && entries[j].length == head.length) {
                merged = tryMerge(head.annotation, entries[j++].annotation)
            }
            if (merged == null) {
                i++
            } else {
                entries.removeAt(j - 1)
                entries[i] = head.copy(annotation = merged)
            }
        }
    }

    private fun tryMerge(a: SourceAnnotation, b: SourceAnnotation): SourceAnnotation? {
        if (a is Style && b is Style) {
            return Style(a.styleClass + b.styleClass)
        }
        if (a is BindingRef && b is BindingRef) {
            // one method can override multiple supers
            if (a.type == BindingRefType.SUPER_METHOD && b.type == BindingRefType.SUPER_METHOD) return null
            if (a.type == BindingRefType.SUPER_TYPE && b.type == BindingRefType.SUPER_TYPE) return null
            // lambdas have a SUPER_TYPE and SUPER_METHOD ref on the same node
            if ((a.type == BindingRefType.SUPER_TYPE && b.type == BindingRefType.SUPER_METHOD) ||
                    (b.type == BindingRefType.SUPER_TYPE && a.type == BindingRefType.SUPER_METHOD)) return null
            throw RuntimeException("Duplicate ref: $a / $b")
        }
        return null
    }
}