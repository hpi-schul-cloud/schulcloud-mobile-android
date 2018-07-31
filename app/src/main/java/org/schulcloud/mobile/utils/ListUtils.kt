package org.schulcloud.mobile.utils

fun <T> List<T>.limit(limit: Int): List<T> {
    return subList(0, Math.min(limit, size))
}

fun <K, V> Map<out K?, V>.filterKeysNotNull(): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for ((key, value) in this)
        if (key != null)
            result[key] = value
    return result
}
