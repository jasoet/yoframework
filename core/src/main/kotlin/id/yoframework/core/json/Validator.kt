package id.yoframework.core.json

import id.yoframework.core.extension.json.get
import io.vertx.core.json.JsonObject
import kotlin.reflect.KClass

abstract class Validator<T : Any>(
    open val clazz: KClass<T>,
    open val json: JsonObject,
    open val path: String
) {
    protected fun getLastPath(): Pair<JsonObject, String>? {
        val paths = path.split(".")
        return when {
            paths.size == 1 -> json to path
            paths.size > 1 -> {
                val parents = paths.subList(0, paths.size - 1)
                val jsonParent = try {
                    parents.fold(json as JsonObject?) { item, parent ->
                        item?.getJsonObject(parent)
                    } ?: throw NullPointerException()
                } catch (e: Exception) {
                    throw ParentNotFoundException("Parent path [${parents.joinToString(".")}] not found.", e)
                }

                jsonParent to paths.last()
            }
            else -> null
        }
    }

    abstract fun validate(): T
}

open class NonNullValidator<T : Any>(
    override val clazz: KClass<T>,
    override val json: JsonObject,
    override val path: String,
    open val message: String = "$path cannot be null"
) : Validator<T>(clazz, json, path) {
    override fun validate(): T {
        try {
            val (item, lastPath) = getLastPath() ?: throw NullValueException(message = message)
            return item.get(clazz, lastPath) ?: throw NullValueException(message = message)
        } catch (e: ClassCastException) {
            throw ClassIncompatibleException(
                "Value from path [$path] is incompatible with ${clazz.qualifiedName} class.", e
            )
        }
    }
}

class RegexValidator(
    override val json: JsonObject,
    override val path: String,
    private val pattern: Regex,
    override val message: String = "$path not match with $pattern"
) : NonNullValidator<String>(String::class, json, path) {
    override fun validate(): String {
        val value = super.validate()
        return if (pattern.matchEntire(value) != null) {
            value
        } else {
            throw  PatternException(message, pattern.toString(), value)
        }
    }
}



