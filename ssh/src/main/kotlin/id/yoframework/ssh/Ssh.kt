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

package id.yoframework.ssh

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelDirectTCPIP
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelForwardedTCPIP
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import id.yoframework.core.extension.resource.homeDir
import java.io.File
import java.io.File.separator
import java.util.Properties

typealias Ssh = JSch

enum class ChannelType(val type: String) {
    SESSION("session"),
    SHELL("shell"),
    EXEC("exec"),
    X11("x11"),
    AGENT_FORWARDING("auth-agent@openssh.com"),
    DIRECT_TCP_IP("direct-tcpip"),
    FORWARDED_TCP_IP("forwarded-tcpip"),
    SFTP("sftp"),
    SUBSYSTEM("subsystem");
}

fun privateKeyLocation(homeDir: String = homeDir(), path: String = ".ssh${separator}id_rsa"): String {
    return "$homeDir$separator$path"
}

fun knownHostLocation(homeDir: String = homeDir(), path: String = ".ssh${separator}known_hosts"): String {
    return "$homeDir$separator$path"
}

fun Session.setConfig(propertyMap: Map<String, String>) {
    val config = Properties().apply {
        put("StrictHostKeyChecking", "no")
        putAll(propertyMap)
    }

    this.setConfig(config)
}

fun createSsh(
    privateKeyLocation: Pair<String, String> = privateKeyLocation() to "",
    knownHostLocation: String = knownHostLocation(),
    customize: (Ssh) -> Unit = {}
): Ssh {
    val ssh = Ssh()
    if (File(privateKeyLocation.first).exists()) {
        ssh.addIdentity(privateKeyLocation.first, privateKeyLocation.second)
    }
    if (File(knownHostLocation).exists()) {
        ssh.setKnownHosts(knownHostLocation)
    }
    customize(ssh)
    return ssh
}

fun Ssh.createSession(
    user: String,
    host: String,
    port: Int = 22,
    daemonThread: Boolean = false,
    properties: Map<String, String> = emptyMap(),
    customize: (Session) -> Unit = {}
): Session {
    val session = this.getSession(user, host, port)
    session.setDaemonThread(daemonThread)
    session.setConfig(properties)
    customize(session)
    return session
}

suspend fun Session.use(timeout: Int = 0, operation: suspend Session.() -> Unit) {
    try {
        if (!this.isConnected) {
            this.connect(timeout)
        }
        operation(this)
    } finally {
        this.disconnect()
    }
}

fun Session.openChannel(type: ChannelType): Channel {
    return this.openChannel(type.type)
}

inline fun <reified T : Channel> Session.openChannel(): T {
    return when (T::class) {
        ChannelShell::class -> this.openChannel(ChannelType.SHELL) as T
        ChannelExec::class -> this.openChannel(ChannelType.EXEC) as T
        ChannelDirectTCPIP::class -> this.openChannel(ChannelType.DIRECT_TCP_IP) as T
        ChannelForwardedTCPIP::class -> this.openChannel(ChannelType.FORWARDED_TCP_IP) as T
        ChannelSftp::class -> this.openChannel(ChannelType.SFTP) as T
        else -> throw IllegalArgumentException("Unsupported Class ${T::class.simpleName}")
    }
}

suspend fun Channel.use(timeout: Int = 0, operation: suspend Channel.() -> Unit) {
    try {
        if (!this.isConnected) {
            this.connect(timeout)
        }
        operation(this)
    } finally {
        this.disconnect()
    }
}
