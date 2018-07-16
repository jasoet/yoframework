/*
 * Copyright (C)2018 - Deny Prasetyo <jasoet87@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package  id.yoframework.extra.extension.codec

import id.yoframework.core.extension.string.abbreviate
import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import org.apache.commons.codec.binary.StringUtils as CodecStringUtils


private val base64Codec = Base64()


fun <T : Serializable> T.serializeToByteArray(): ByteArray {
    return SerializationUtils.serialize(this)
}

fun String.base64Encode(): String {
    return base64Codec.encodeToString(this.toByteArray())
}

fun String.base64EncodeToByteArray(): ByteArray {
    return base64Codec.encode(this.toByteArray())
}

fun <T : Serializable> T.base64Encode(): String {
    return base64Codec.encodeToString(this.serializeToByteArray())
}

fun <T : Serializable> T.base64EncodeToByteArray(): ByteArray {
    return base64Codec.encode(this.serializeToByteArray())
}

fun ByteArray.base64EncodeToString(): String {
    return base64Codec.encodeAsString(this)
}

fun ByteArray.base64Encode(): ByteArray {
    return base64Codec.encode(this)
}

@Throws(DecodeBase64Exception::class)
fun String.base64Decode(): String {
    return try {
        CodecStringUtils.newStringUtf8(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.abbreviate()}", e)
    }
}

@Throws(DecodeBase64Exception::class)
fun <T : Serializable> String.base64DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.abbreviate()}", e)
    }
}

@Throws(DecodeBase64Exception::class)
fun <T : Serializable> ByteArray.base64DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.size} bytes ", e)
    }
}


class DecodeBase64Exception(message: String, cause: Throwable) : Exception(message, cause)