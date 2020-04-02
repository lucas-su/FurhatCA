package furhatos.app.gettingstarted2.flow

import furhatos.app.gettingstarted2.discussionState
import furhatos.app.gettingstarted2.politenessScore
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.gettingstarted2.*
import furhatos.gestures.Gestures
import furhatos.nlu.Intent
import furhatos.util.Language

//start wordt nogsteeds als standaard state gebruikt
val Start : State = state(Interaction) {

    onEntry {
        when (discussionState){
            0 -> goto(Startstate)
            1 -> goto(Freekick)
            2 -> goto(Yellow)
            3 -> goto(Red)
            4 -> goto(EndState)
        }
    }
}

//de gedachte die er nu achter zit: Als de user een keyword zegt zoals yellow, of red, gaat het gesprek naar die state
//vervolgens zegt de robot dingen adhv het politenesslevel. Er zijn vier levels. 0 is polite, 3 is rude.
//dit heb ik voor yellowcard geïmplementeerd nu. Ook zit er een kaart-geef-moment in, aangeduid met boolean cardGiven.
// misschien beetje onlogisch nu is dat politenesslevel omhoog gaat (dus meer rude) als je 'yellow' blijft herhalen.
//eigenlijk bij herhalen moet hij gewoon iets anders zeggen maar met hetzelfde politenesslevel lijkt me, kwestie van 'random'
//zinnen toevoegen

val Startstate = state(Interaction) {
    onEntry {
        if (politenessScore < 1){
            furhat.ask("Hey! Why did you blow the whistle?")
    }   else {
            furhat.ask("<emphasis level=\"strong\">Tell me, why </emphasis> did you blow the wistle?")
        }
    }

    onNoResponse {
        politenessScore += 1
        goto(Start)
    }

    onResponse<Foul> {
        furhat.ask("And what do I get for that?")
    }
    onResponse<yellowCard>{
        discussionState = 2
        goto(Start)
    }
    onResponse<redCard>{
        discussionState = 3
        goto(Start)
    }
    onResponse {
        goto(unknownState)
    }
}

val unknownState = state(Interaction) {
    onEntry {
        furhat.ask("I did not hear you, what happened?")
        }
    onResponse<yellowCard> {
        discussionState = 2
        goto(Start)
    }
    onResponse<redCard> {
        discussionState = 3
        goto(Start)
    }
    onNoResponse {
        politenessScore += 1
        reentry()
    }
    onResponse<Foul> {
        furhat.ask("And what are you going to give me for that?")
    }
}

val Freekick = state(Interaction) {
    onEntry {
        if(cardGiven){
        random(

                {   furhat.say("Thank you, that is nice of you") },
                {   furhat.say("Hmm that seems fair")}
        )
            goto(EndState)
        }
        else{
            random(
                    {furhat.ask("Well, can we maybe get off with a warning this time, mister?")}
            )
        }
        goto(Start)
    }
}

val Yellow = state(Interaction) {
    onEntry {
        if(cardGiven) {
            if(politenessScore == 0){
                furhat.ask("Well OK, maybe you were right...")}
            else if(politenessScore == 1){
                furhat.ask("I disagree, but i have to accept your choice")}
            else if(politenessScore == 2){
                furhat.gesture(Gestures.ExpressAnger)
                furhat.ask("You are exaggerating! The other team must have paid you for this nonsense!")}
            else if(politenessScore > 2){
                furhat.gesture(Gestures.ExpressAnger)
                furhat.ask("I will not forget you, sick man.")}

        goto(EndState)
        } else {
            furhat.gesture(Gestures.BrowFrown)
            if(politenessScore == 0){
                    furhat.ask("Yellow? Could you please reconsider that? I saw that he was acting.")}
            else if(politenessScore == 1){
                    furhat.ask("You really want to give me yellow? I only hit the ball!")}
            else if(politenessScore == 2){
                    furhat.gesture(Gestures.ExpressAnger)
                    furhat.ask("Man you are talking rubbish! You are blind, that pig was acting!")}
            else if(politenessScore > 2){
                    furhat.gesture(Gestures.ExpressAnger)
                    furhat.ask("Yellow!? I will give you corona!")}
            }
        }

//    furhat.listen()
    onResponse<giveCard>{
        cardGiven = true
        goto(Start)
    }

    //free kick state moet hier nog bij

    onResponse<yellowCard>{
        if (politenessScore<3){politenessScore ++}
        discussionState = 2
        goto(Start)
    }
    onResponse<beingPolite>{//blijf wel in dezelfde state, maar reageert beleefder
        if (politenessScore>0){politenessScore --}
        goto(Start)
    }
    onResponse<beingRude>{//blijf wel in dezelfde state, maar reageert onbeleefder
        if (politenessScore<3){politenessScore ++}
        goto(Start)
    }
    onResponse<redCard>{
        discussionState = 3
        goto(Start)
    }
    onNoResponse{
        discussionState = 2
        goto(Start)
    }
    onResponse {
        random(
                {furhat.ask("What did you say? The crowd makes so much noise, i did not get that.")},
                {furhat.ask("Could you repeat that? It is so noisy here.")},
                {furhat.ask("Repeat that loud and clear, buddy")}

        )
        when (sentiment(it.text)){
            0 -> furhat.say("something nice")
        }
        goto(Start)
    }
}

val Red = state(Interaction) {
    onEntry {
        furhat.gesture(Gestures.ExpressAnger)
        if(cardGiven){
        random(
                {   furhat.say("What?! A red card? That is so unfair!") },
                {   furhat.say("No, not a red card! Get corona!") }
        )
            goto(EndState)
        }
        else {
            random(
                    {
                        furhat.ask("Red?! That would be so unfair!")
                    },
                    {furhat.ask("Everyone knows that i do not deserve red here!")}
            )
        }
        //goto(Start)
    }
}

val EndState = state(Interaction) {
    onEntry {
        furhat.gesture(Gestures.BigSmile)
        random(

                {   furhat.say("Let's continue the game.") },
                {   furhat.say("Let's continue.") }
        )

        //goto(Start)
    }
}

//
//package furhatos.app.fruitseller.flow
//
//import furhatos.app.fruitseller.nlu.*
//import furhatos.app.fruitseller.order

//val Start = state(Interaction) {
//    onEntry {
//        random(
//                {   furhat.say("Hi there") },
//                {   furhat.say("Oh, hello there") }
//        )
//
//        goto(TakingOrder)
//    }
//}
//
//val Options = state(Interaction) {
//    onResponse<BuyFruit> {
//        val fruits = it.intent.fruits
//        if (fruits != null) {
//            goto(OrderReceived(fruits))
//        }
//        else {
//            propagate()
//        }
//    }
//
//    onResponse<RequestOptions> {
//        furhat.say("We have ${Fruit().optionsToText()}")
//        furhat.ask("Do you want some?")
//    }
//
//    onResponse<Yes> {
//        random(
//                { furhat.ask("What kind of fruit do you want?") },
//                { furhat.ask("What type of fruit?") }
//        )
//    }
//}
//
//val TakingOrder = state(Options) {
//    onEntry {
//        random(
//                { furhat.ask("How about some fruits?") },
//                { furhat.ask("Do you want some fruits?") }
//        )
//    }
//
//    onResponse<No> {
//        furhat.say("Okay, that's a shame. Have a splendid day!")
//        goto(Idle)
//    }
//}
//
//fun OrderReceived(fruits: FruitList) : State = state(Options) {
//    onEntry {
//        furhat.say("${fruits.text}, what a lovely choice!")
//        fruits.list.forEach {
//            users.current.order.fruits.list.add(it)
//        }
//        furhat.ask("Anything else?")
//    }
//
//    onReentry {
//        furhat.ask("Did you want something else?")
//    }
//
//    onResponse<No> {
//        furhat.say("Okay, here is your order of ${users.current.order.fruits}. Have a great day!")
//    }
//}