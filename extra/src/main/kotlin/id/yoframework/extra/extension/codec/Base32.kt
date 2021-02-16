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
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable

private val base32Codec = Base32()

fun String.base32Encode(): String {
    return base32Codec.encodeToString(this.toByteArray())
}

fun String.base32EncodeToByteArray(): ByteArray {
    return base32Codec.encode(this.toByteArray())
}


fun <T : Serializable> T.base32Encode(): String {
    return base32Codec.encodeToString(this.serializeToByteArray())
}

fun <T : Serializable> T.base32EncodeToByteArray(): ByteArray {
    return base32Codec.encode(this.serializeToByteArray())
}

fun ByteArray.base32EncodeToString(): String {
    return base32Codec.encodeAsString(this)
}

fun ByteArray.base32Encode(): ByteArray {
    return base32Codec.encode(this)
}

@Throws(DecodeBase32Exception::class)
fun String.base32Decode(): String {
    return try {
        StringUtils.newStringUtf8(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.abbreviate()} ", e)
    }

}

@Throws(DecodeBase32Exception::class)
fun <T : Serializable> String.base32DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.abbreviate()} ", e)
    }
}

@Throws(DecodeBase32Exception::class)
fun <T : Serializable> ByteArray.base32DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.size} bytes ", e)
    }
}


class DecodeBase32Exception(message: String, cause: Throwable) : Exception(message, cause)