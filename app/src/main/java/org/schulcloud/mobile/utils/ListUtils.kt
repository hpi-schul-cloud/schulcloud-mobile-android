package org.schulcloud.mobile.utils

fun <K, V> Map<out K?, V>.filterKeysNotNull(): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for ((key, value) in this)
        if (key != null)
            result[key] = value
    return result
}
