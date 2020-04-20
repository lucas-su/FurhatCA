package furhatos.app.gettingstarted2

import furhatos.app.gettingstarted2.flow.Idle
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence


val updateSentiment = MQTT()
var discussionState = 0 // 0 -> unknown 1 -> free kick 2 -> yellow card 3 -> red card
var politenessScore = 0 // 0 -> most polite 3 -> most rude
var cardGiven = false   // boolean to separate states before and after the card was given
var difficulty = 4      // default difficulty

class Gettingstarted2Skill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}


class MQTT : MqttCallback {
    private val qos = 1
    private var topic = "/furhat/input"

    val client: MqttClient
    private val password = "" // See canvas comment for password

    init {
        val host = String.format("tcp://%s:%d", "lucschootuiterkamp.nl", 1884)
        val clientId = "FH"
        val conOpt = MqttConnectOptions()
        conOpt.isCleanSession = true
        conOpt.userName = "furhat"
        conOpt.password = password.toCharArray()
        conOpt.keepAliveInterval = 2
        client = MqttClient(host, clientId, MemoryPersistence())
        client.setCallback(this)
        while (!client.isConnected){
            client.connect(conOpt)
        }
        client.subscribe("/furhat/output", qos)
    }

    @Throws(MqttException::class)
    fun input(payload: String) {                        // named input to make usage clearer in usage such that it is used as updateSentiment().input(it.text)
        val message = MqttMessage(payload.toByteArray())
        message.qos = qos
        println("publish")
        client.publish(this.topic, message)             // blocking publish
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
        when (String(message.payload)){
            "pos" -> {
                politenessScore = kotlin.math.max(politenessScore - 1, 0)
                println("new politeness score: " + politenessScore)
            }
            "neu" -> {if (Math.random()*25 < difficulty) { // 25 balances it a bit more than 10
                politenessScore = kotlin.math.min(politenessScore + 1, 3)
                println("new politeness score: " + politenessScore)     // a higher difficulty makes it more likely
            } else if (Math.random()<0.5) {                             // that neutral phrases will be interpreted as hostile
                politenessScore = kotlin.math.max(politenessScore - 1, 0)
                println("new politeness score: " + politenessScore)
            }
            }
            "neg" -> if (Math.random() < 0.5) { // only let negative comments affect politenessScore half of the time
                politenessScore = kotlin.math.min(politenessScore + 1, 3)
                println("new politeness score: " + politenessScore)
            }
            else ->{
            }
        }
    }
}

