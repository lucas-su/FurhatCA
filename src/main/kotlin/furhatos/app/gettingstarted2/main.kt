<<<<<<< HEAD
package furhatos.app.gettingstarted2

import furhatos.app.gettingstarted2.flow.Idle
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.net.URI
import java.net.URISyntaxException

fun sentiment(char: String): Int {
    return 1
}

var discussionState = 0 // start == 0 free kick == 1 yellow card == 2 red card == 3
var politenessScore = 0 //
var cardGiven = false // boolean to separate states before and after the card was given

class Gettingstarted2Skill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Subscriber().sendMessage("you're getting a yellow card")
    Skill.main(args)
}


/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client blocking API.
 */
class Subscriber() : MqttCallback {
    private val qos = 1
    private var topic = "/furhat/input"
    private val client: MqttClient

//    constructor(uri: String?) : this(URI(uri)) {}

//    private fun getAuth(uri: URI): Array<String> {
//        val a = uri.authority
//        val first = a.split("@").toTypedArray()
//        return first[0].split(":").toTypedArray()
//    }

    @Throws(MqttException::class)
    fun sendMessage(payload: String) {
        val message = MqttMessage(payload.toByteArray())
        message.qos = qos
        client.publish(topic, message) // Blocking publish
    }

    /**
     * @see MqttCallback.connectionLost
     */
    override fun connectionLost(cause: Throwable) {
        println("Connection lost because: $cause")
        System.exit(1)
    }

    /**
     * @see MqttCallback.deliveryComplete
     */
    override fun deliveryComplete(token: IMqttDeliveryToken) {}

    /**
     * @see MqttCallback.messageArrived
     */
    @Throws(MqttException::class)
    override fun messageArrived(topic: String, message: MqttMessage) {
        println(String.format("[%s] %s", topic, String(message.payload)))
    }

//    companion object {
//        @Throws(MqttException::class, URISyntaxException::class)
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val s = Subscriber(System.getenv("CLOUDMQTT_URL"))
//            s.sendMessage("Hello")
//            s.sendMessage("Hello 2")
//        }
//    }

    init {
        val host = String.format("tcp://%s:%d", "lucschootuiterkamp.nl", 1884)
//        val auth = getAuth(uri)
//        val username = auth[0]
//        val password = auth[1]
        val clientId = "Furhat"
//        if (!uri.path.isEmpty()) {
//            topic = uri.path.substring(1)
//        }
        val conOpt = MqttConnectOptions()
        conOpt.isCleanSession = true
        conOpt.userName = "furhat"
        conOpt.password = "weHRUUNCT5jHFpjcsAq".toCharArray()
        client = MqttClient(host, clientId, MemoryPersistence())
        client.setCallback(this)
        client.connect(conOpt)
        client.subscribe(topic, qos)
    }
=======
package furhatos.app.gettingstarted2

import furhatos.app.gettingstarted2.flow.Idle
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.net.URI
import java.net.URISyntaxException

fun sentiment(char: String): Int {
    return 1
}

var discussionState = 0 // start == 0 free kick == 1 yellow card == 2 red card == 3
var politenessScore = 0 //
var cardGiven = false // boolean to separate states before and after the card was given

class Gettingstarted2Skill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Subscriber().sendMessage("you're getting a yellow card")
    Skill.main(args)
}


/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client blocking API.
 */
class Subscriber() : MqttCallback {
    private val qos = 1
    private var topic = "/furhat/input"
    private val client: MqttClient

//    constructor(uri: String?) : this(URI(uri)) {}

//    private fun getAuth(uri: URI): Array<String> {
//        val a = uri.authority
//        val first = a.split("@").toTypedArray()
//        return first[0].split(":").toTypedArray()
//    }

    @Throws(MqttException::class)
    fun sendMessage(payload: String) {
        val message = MqttMessage(payload.toByteArray())
        message.qos = qos
        client.publish(topic, message) // Blocking publish
    }

    /**
     * @see MqttCallback.connectionLost
     */
    override fun connectionLost(cause: Throwable) {
        println("Connection lost because: $cause")
        System.exit(1)
    }

    /**
     * @see MqttCallback.deliveryComplete
     */
    override fun deliveryComplete(token: IMqttDeliveryToken) {}

    /**
     * @see MqttCallback.messageArrived
     */
    @Throws(MqttException::class)
    override fun messageArrived(topic: String, message: MqttMessage) {
        println(String.format("[%s] %s", topic, String(message.payload)))
    }

//    companion object {
//        @Throws(MqttException::class, URISyntaxException::class)
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val s = Subscriber(System.getenv("CLOUDMQTT_URL"))
//            s.sendMessage("Hello")
//            s.sendMessage("Hello 2")
//        }
//    }

    init {
        val host = String.format("tcp://%s:%d", "lucschootuiterkamp.nl", 1884)
//        val auth = getAuth(uri)
//        val username = auth[0]
//        val password = auth[1]
        val clientId = "Furhat"
//        if (!uri.path.isEmpty()) {
//            topic = uri.path.substring(1)
//        }
        val conOpt = MqttConnectOptions()
        conOpt.isCleanSession = true
        conOpt.userName = "furhat"
        conOpt.password = "weHRUUNCT5jHFpjcsAq".toCharArray()
        client = MqttClient(host, clientId, MemoryPersistence())
        client.setCallback(this)
        client.connect(conOpt)
        client.subscribe(topic, qos)
    }
>>>>>>> 6e4b38f06b05dd549d5f2b7b35d917501daa3f5c
}