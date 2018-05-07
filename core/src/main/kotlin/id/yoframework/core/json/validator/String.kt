package id.yoframework.core.json.validator

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import id.yoframework.core.json.get
import id.yoframework.core.json.validator.config.Length
import id.yoframework.core.json.validator.error.EmailError
import id.yoframework.core.json.validator.error.FormatError
import id.yoframework.core.json.validator.error.LengthError
import id.yoframework.core.json.validator.error.ValidationError
import io.vertx.core.json.JsonObject

fun JsonObject.format(key: String, format: Regex): Validated<ValidationError, String> {
    val value = this[key] ?: ""
    return if (format.matchEntire(value) != null) {
        value.valid()
    } else {
        FormatError(
            "$key doesn't match pattern [$format]",
            format.toString(),
            value
        ).invalid()
    }
}

fun JsonObject.email(key: String): Validated<ValidationError, String> {
    val emailRegex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$".toRegex()
    return this.format(key, emailRegex).leftMap {
        EmailError("$key is not a valid email")
    }
}

fun JsonObject.length(key: String, config: Length): Validated<ValidationError, String> {
    val value = this[key] ?: ""
    val length = value.length
    val valid = when (config) {
        is Length.Exactly -> length == config.value
        is Length.Maximum -> length <= config.value
        is Length.Minimum -> length >= config.value
        is Length.Range -> length >= config.from && length <= config.to
    }
    return if (valid) value.valid() else LengthError("$key length is invalid", config).invalid()
}
