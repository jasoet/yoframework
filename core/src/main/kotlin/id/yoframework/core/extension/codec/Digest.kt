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

package id.yoframework.core.extension.codec

import org.apache.commons.codec.digest.DigestUtils
import java.io.InputStream

/**
 * Calculate SHA1 Digest as Hexadecimal
 * Do not close [InputStream] after use nor reset
 * please handle it yourself
 *
 * @return Hexadecimal Digest from [InputStream] as String
 */
fun InputStream.sha1HexDigest(): String {
    return DigestUtils.sha1Hex(this)
}

/**
 * Calculate SHA1 Digest as Hexadecimal
 *
 * @return Hexadecimal Digest from [ByteArray] as String
 */
fun ByteArray.sha1HexDigest(): String {
    return DigestUtils.sha1Hex(this)
}
