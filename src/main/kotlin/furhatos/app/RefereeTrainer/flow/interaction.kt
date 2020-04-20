package furhatos.app.gettingstarted2.flow

import furhatos.app.gettingstarted2.discussionState
import furhatos.app.gettingstarted2.politenessScore
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.gettingstarted2.*
import furhatos.autobehavior.*
import furhatos.flow.kotlin.voice.PollyVoice
import furhatos.gestures.Gestures
import org.apache.commons.lang.math.NumberUtils.toInt


var skipAsk = 0 // if flag is set, it can be used to prevent execution of ask upon reentry, for example from onResponse<Foul> in unknownstate,
                // this would be nicer if you could do this with reentry() but i do not know how
                // also it is not nice that this is global
var skipIntro = false // skip explanation for development

// initialize buttons
val freekickButton = Button("freekick", key = "f")
val yellowCardButton = Button("yellow", key = "y")
val redCardButton = Button("red", key = "r")

val Start : State = state(Interaction) {
    onEntry {
        if(skipIntro){
            furhat.setTexture("Marty")
            goto(stateDealer)
        } else{
//        furhat.ledStrip.solid(java.awt.Color.RED) // this apparently only works with the real robot
        furhat.setTexture("Ivan")
        furhat.voice = PollyVoice.Brian()
        furhat.gesture(Gestures.Smile)
        furhat.say("Hello. Welcome to the furhat referee training simulation." )
        furhat.gesture(Gestures.BrowRaise)
        furhat.say ( "My name is Marvin the Android. I will explain to you how this simulation works.<break/>" )
        furhat.say("This simulation will test out your capabilities to remain polite and calm when a player asks something of you.<break/> " )
        furhat.say("In this situation, you are a referee in a football match, and one player has just kicked another player.<break time=\"1s\"/>")
        furhat.say("Your first inclination is to give the player a yellow card, but you are free to give a free kick or a red card instead. " +
                "Your goal in this simulation is to speak to the player, and to tell him what your decision is. " +
                "The player will respond to the way you phrase things, so be mindful to remain polite.")
        furhat.gesture(Gestures.Smile)
        furhat.say("If you have made your final decision, say <emphasis level=\"moderate\"> \"this is my final decision\"</emphasis><break/>")
        furhat.say("Alternatively, if you have made your final decision, you can use the keyboard to tell the player about the decision.<break/>")
        furhat.say("You can use the \'f\' key to indicate a free kick, the \'y\' key to indicate a yellow card and the \'r\' key to indicate a red card at any time if you have the wizard screen open. <break/>")
        furhat.ask("The difficulty of this simulation can be set, ranging from 1 to 10. Please indicate the level of difficulty. ")
        }
    }
    onReentry {
        furhat.listen()
    }
    onResponse{
        difficulty = try {toInt(it.text)} catch (e: NumberFormatException) {7}
        if (difficulty == 0){
            furhat.say("That does not sound like a number between one and ten to me, I will make it easy for you.")
            difficulty = 4 // if furhat does not understand you the toInt functions sometimes returns 0 which does not work, so we set the default 4
        }                  // it also sometimes recognizes 4 as four
        furhat.say("The difficulty has been set to %d".format(difficulty))
        furhat.say("Let's start the simulation,<break/> good luck! <break time=\"3s\"/>")
        furhat.setTexture("Marty")          // switch to simulation agent model
        furhat.voice = PollyVoice.Joey()    // switch to simulation agent voice
        goto(stateDealer)
    }
}

val stateDealer : State = state(Interaction){
    // generalized state update based on politeness here would be nice
    // use gesture async to continue speaking as well
    onEntry {
        furhat.userSpeechStartGesture = listOf(Gestures.BrowFrown, Gestures.Oh, Gestures.BrowRaise)
        when (discussionState){
            0 -> goto(stateUnknown)
            1 -> goto(Freekick)
            2 -> goto(Yellow)
            3 -> goto(Red)
            4 -> goto(EndState)
        }
    }
}

val stateUnknown = state(Interaction) {
    onEntry {
        if (politenessScore < 1){
            furhat.gesture(Gestures.Surprise(duration=5.0,strength=2.0))
            furhat.ask("<prosody volume=\"loud\" pitch=\"high\">Hey! Why did you blow the whistle?</prosody>")
        }   else {
            furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=2.0))
            furhat.ask("<emphasis level=\"strong\">Tell me, why </emphasis> did you blow the wistle?")
        }
    }
    onReentry() {
        when {
            skipAsk == 1 -> {
                skipAsk = 0
                furhat.listen()
            }
            reentryCount <3 -> {
                furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=2.0))
                when (politenessScore){
                    0 -> {
                        furhat.ask("I am sorry, I think that i did not hear you, what happened?")
                    }
                    1 -> {
                        furhat.ask("Those words do not make any sense to me. What?")
                    }
                    2 -> {
                        furhat.ask("What are you saying? In English please.")
                    }
                    else -> {
                        furhat.ask("What language is that? Learn to communicate before you start wasting my time.")
                    }
                }
            }
            else -> { furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=5.0))
                random(
                        {furhat.ask("You should speak clearer, I still cannot hear you")},
                        {furhat.ask("Speak up, you're still impossible to understand")}
                )
            }
        }
    }
    onResponse<Freekick>{//if you say a 'freekick' intent in your answer
        updateSentiment.input(it.text)
        discussionState = 1
        goto(stateDealer)
    }
    onResponse<Foul> {//if you say a 'foul' intent in your answer
        updateSentiment.input(it.text)
        random(
                { furhat.say("And what are you going to give me for that?")},
                {furhat.say("And what does that earn me?")}
        )
        skipAsk = 1
        reentry()
    }
    onResponse<yellowCard>{     // match yellow intent
        updateSentiment.input(it.text)
        furhat.gesture(Gestures.Surprise(duration=5.0,strength=2.0))
        discussionState = 2
        goto(stateDealer)
    }
    onResponse<redCard>{        // match red intent
        updateSentiment.input(it.text)
        discussionState = 3
        goto(stateDealer)
    }
// below is a remnant of hard coding politeness

//    onResponse<beingPolite>{ // remnant of hard coding politeness
//        updateSentiment.input(it.text)
//        politenessScore = kotlin.math.max(politenessScore-1, 3)
//        goto(stateDealer)
//    }
//    onResponse<beingRude>{    // remnant of hard coding politeness
//        politenessScore = kotlin.math.min(politenessScore+1, 3)
//        goto(stateDealer)
//    }

    onResponse<giveCard>{       // match final answer intent
        cardGiven = true
        goto(stateDealer)
    }
    onNoResponse {// on no response
        if (Math.random() < 0.3) { // sometimes update score because you don't say anything back and that's rude
            politenessScore = kotlin.math.min(politenessScore + 1, 3)
        }
        reentry()
    }
    onResponse {//for any other response
        updateSentiment.input(it.text)
        reentry()
    }
}

val Freekick = state(Interaction) {
    onEntry {
        if (cardGiven) {
            furhat.gesture(Gestures.GazeAway(duration = 1.0))
            when (politenessScore) {
                0 -> {
                    furhat.say("Well OK, maybe you were right...")
                }
                1 -> {
                    furhat.say("Hmm, that seems fair.")
                }
                2 -> {
                    furhat.gesture(Gestures.ExpressDisgust)
                    furhat.say("<prosody volume=\"loud\">I disagree. The other team must have paid you for this nonsense!</prosody>")
                }
                else -> {
                    furhat.gesture(Gestures.ExpressDisgust)
                    furhat.say("<prosody volume=\"loud\">You are exaggerating!</prosody>")
                }
            }
            goto(EndState)
        } else {
            when (politenessScore) {
                0 -> {
                    furhat.gesture(Gestures.Wink)
                    furhat.ask("I think even a free kick would be too much. But I guess you are right, I will pay more attention next time.")
                }
                1 -> {
                    furhat.gesture(Gestures.Smile(duration = 5.0, strength = 2.0))
                    furhat.ask("A free kick? I did nothing wrong.")
                }
                2 -> {
                    furhat.gesture(Gestures.ExpressDisgust(duration = 5.0, strength = 1.0))
                    furhat.ask("<prosody volume=\"loud\">I barely touched that coward! Don't even think of given them a free kick!</prosody>")
                }
                else -> {
                    furhat.gesture(Gestures.ExpressAnger(duration = 5.0, strength = 1.0))
                    furhat.ask("A free kick?<prosody volume=\"loud\"> I will give you a kick for free!</prosody>")
                }
            }
            }
        }
    onResponse<Yes>{        // if you answer yes to the question above about the final decision
        cardGiven = true
        goto(EndState);
    }
    onResponse<No>{     // if you answer no to the question above about the final decision
        updateSentiment.input(it.text)
        goto(stateDealer);
    }
    onResponse<Foul>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<Freekick>{
        updateSentiment.input(it.text)
        discussionState = 1
        goto(stateDealer)
    }
    onResponse<yellowCard>{
        updateSentiment.input(it.text)
        discussionState = 2
        goto(stateDealer)
    }
    onResponse<redCard>{
        updateSentiment.input(it.text)
        discussionState = 3
        goto(stateDealer)
    }
    onResponse<giveCard>{
        updateSentiment.input(it.text)
        cardGiven = true
        goto(stateDealer)
    }
//    onResponse<beingPolite>{
//        politenessScore = kotlin.math.max(politenessScore-1, 3)
//        goto(stateDealer)
//    }
//    onResponse<beingRude>{//blijf wel in dezelfde state, maar reageert onbeleefder
//        politenessScore = kotlin.math.min(politenessScore+1, 3)
//        goto(stateDealer)
//    }
    onResponse<disagreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<agreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onNoResponse{
        discussionState = 1
        goto(stateDealer)
    }
    onResponse {
        if(politenessScore < 2){
            furhat.gesture(Gestures.Thoughtful(duration=5.0,strength=2.0))
            furhat.ask("What did you say? The crowd makes so much noise, i did not get that.")
        }else {
            furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=2.0))
            furhat.ask("Repeat that. There is too much noise here. Man this is getting annoying.")
        }
        goto(stateDealer)
    }
    onButton(freekickButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(yellowCardButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(redCardButton){
        cardGiven = true
        goto(EndState)
    }
}


val Yellow = state(Interaction) {
    onEntry {
        if (cardGiven) {
            furhat.gesture(Gestures.GazeAway(duration = 1.0))
            when (politenessScore){
                0 -> {
                    furhat.say("Well OK, maybe you were right...")
                }
                1 -> {
                    furhat.say("I disagree, but i have to accept your choice")
                }
                2 -> {
                    furhat.gesture(Gestures.ExpressAnger)
                    furhat.say("<prosody volume=\"loud\">You are exaggerating!</prosody> The other team must have paid you for this nonsense!")
                }
                else -> {
                    furhat.gesture(Gestures.ExpressAnger)
                    furhat.say("I will not forget you, sick man.")
                }
            }
            goto(EndState)
        } else {
            when (politenessScore) {
                0 -> {
                    furhat.gesture(Gestures.ExpressFear(duration = 5.0))
                    furhat.ask("Yellow? Could you please reconsider that? I saw that he was acting.")
                }
                1 -> {
                    furhat.gesture(Gestures.Surprise(duration = 5.0))
                    furhat.ask("You really want to give me yellow? I only hit the ball!")
                }
                2 -> {
                    furhat.gesture(Gestures.ExpressAnger(duration = 5.0, strength = 1.0))
                    random(
                            {furhat.ask("<prosody volume=\"loud\">Man you are talking rubbish! </prosody>You are blind, that pig was acting!")},
                            {furhat.ask("Shut up, that was not deserved")}
                    )
                }
                else -> {
                    furhat.gesture(Gestures.ExpressAnger(duration = 5.0, strength = 2.0))
                    random(
                            {furhat.ask("<prosody volume=\"loud\" pitch = \"high\">No way!</prosody> you are the worst referee")},
                            {furhat.ask("<prosody volume=\"loud\" pitch = \"high\">Yellow!?</prosody> I will give you corona!")}
                    )
                }
            }
        }
    }
    onResponse<Yes>{
        updateSentiment.input(it.text)
        when (politenessScore) {
            0 -> {
                // yes to reconsideration
                discussionState = 1
                goto(stateDealer)
            }
            1 -> {
                // yes to give me yellow
                politenessScore = kotlin.math.min(politenessScore+1, 3)
                discussionState = 2
                goto(stateDealer)
            }
            else -> { // if yes is said in another condition, reentry
                reentry()
            }
        }
    }
    onResponse<No>{
        updateSentiment.input(it.text)
        when (politenessScore) {
            0 -> {
                // no to reconsideration
                politenessScore = kotlin.math.min(politenessScore+1, 3)
                discussionState = 2
                goto(stateDealer)
            }
            1 -> {
                // no to give me yellow
                cardGiven = true
                goto(Freekick)
            }
            else -> {
                reentry()
            }
        }
    }
    onResponse<giveCard>{
        updateSentiment.input(it.text)
        cardGiven = true
        goto(stateDealer)
    }
    onResponse<Foul>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<Freekick>{
        updateSentiment.input(it.text)
        if (politenessScore>0){politenessScore --}
        discussionState = 1
        goto(stateDealer)
    }
    onResponse<yellowCard>{
        updateSentiment.input(it.text)
        discussionState = 2
        goto(stateDealer)
    }
    onResponse<redCard>{
        updateSentiment.input(it.text)
        discussionState = 3
        goto(stateDealer)
    }
//    onResponse<beingPolite>{//blijf wel in dezelfde state, maar reageert beleefder
//        updateSentiment.input(it.text)
//        if (politenessScore>0){politenessScore --}
//        goto(stateDealer)
//    }
//    onResponse<beingRude>{//blijf wel in dezelfde state, maar reageert onbeleefder
//        politenessScore = kotlin.math.min(politenessScore+1, 3)
//        goto(stateDealer)
//    }
    onResponse<disagreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<agreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onNoResponse{
        discussionState = 2
        goto(stateDealer)
    }
    onResponse {
        if(politenessScore < 2){
            furhat.gesture(Gestures.Thoughtful(duration=5.0,strength=2.0))
            furhat.ask("What did you say? The crowd makes so much noise, i did not get that.")
        }else {
            furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=2.0))
            furhat.ask("Repeat that. There is too much noise here. Man this is getting annoying.")
        }
        goto(stateDealer)
    }
    onButton(freekickButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(yellowCardButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(redCardButton){
        cardGiven = true
        goto(EndState)
    }
}

val Red = state(Interaction) {
    onEntry {
        if(cardGiven){
            when (politenessScore) {
                0 -> {
                    furhat.gesture(Gestures.GazeAway(duration=2.0,strength=0.3))
                    furhat.say("Red? I strongly disagree with that but i have to accept your choice.")}
                1 -> {
                    furhat.gesture(Gestures.ExpressDisgust(duration=3.0,strength=2.0))
                    furhat.say("<prosody pitch =\"high\">What?! A red card? </prosody>That is so unfair!")}
                2 -> {
                    furhat.gesture(Gestures.ExpressAnger)
                    furhat.say("<prosody volume=\"loud\">You are exaggerating!</prosody> The other team must have paid you for this nonsense!")}
                else -> {
                    furhat.gesture(Gestures.ExpressAnger)
                    random({furhat.say("<prosody volume=\"loud\" pitch = \"high\">No way!!</prosody> You are the worst referee!")},
                            {furhat.say("<prosody volume=\"loud\">No, not a red card! Get corona!</prosody>")}
                    )
                }
            }
            goto(EndState)
        }
        else {
            when (politenessScore) {
                0 -> {
                    furhat.gesture(Gestures.ExpressFear(duration=4.0,strength=2.0))
                    furhat.ask("Red? I am convinced that he was acting. Giving me red would be a mistake.")}
                1 -> {
                    furhat.gesture(Gestures.ExpressFear(duration=3.0))
                    furhat.ask("<prosody pitch =\"high\">Are you considering red?!</prosody> That would be so unfair! I only hit the ball!")}
                2 -> {
                    furhat.gesture(Gestures.ExpressDisgust(duration=5.0))
                    furhat.ask("<prosody volume=\"loud\">Man you are talking rubbish! </prosody>Everyone knows that i do not deserve red here!")}
                else -> {
                    furhat.gesture(Gestures.ExpressAnger(duration=5.0))
                    furhat.ask("Red!? <prosody volume=\"loud\">I will give you corona if you give me red!</prosody>")}
            }
        }
    }
    onResponse<giveCard>{
        updateSentiment.input(it.text)
        cardGiven = true
        goto(stateDealer)
    }
    onResponse<Foul>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<Freekick>{
        updateSentiment.input(it.text)
        politenessScore = kotlin.math.max(politenessScore-1, 3)
        discussionState = 0
        goto(stateDealer)
    }
    onResponse<yellowCard>{
        updateSentiment.input(it.text)
        discussionState = 2
        goto(stateDealer)
    }
    onResponse<redCard>{
        updateSentiment.input(it.text)
        discussionState = 3
        goto(stateDealer)
    }
//    onResponse<beingPolite>{//blijf wel in dezelfde state, maar reageert beleefder
//        updateSentiment.input(it.text)
//        if (politenessScore>0){politenessScore --}
//        goto(stateDealer)
//    }
//    onResponse<beingRude>{//blijf wel in dezelfde state, maar reageert onbeleefder
//        if (politenessScore<3){politenessScore +1}
//        goto(stateDealer)
//    }
    onResponse<disagreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onResponse<agreeWithPlayer>{
        updateSentiment.input(it.text)
        goto(stateDealer)
    }
    onNoResponse{
        discussionState = 3
        goto(stateDealer)
    }
    onResponse {
        if(politenessScore < 2){
            furhat.gesture(Gestures.Thoughtful(duration=5.0,strength=2.0))
            furhat.ask("What did you say? The crowd makes so much noise, i did not get that.")
        }else {
            furhat.gesture(Gestures.BrowFrown(duration=5.0,strength=2.0))
            furhat.ask("Repeat that. There is too much noise here. Man this is getting annoying.")
        }
        goto(stateDealer)
    }
    onButton(freekickButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(yellowCardButton){
        cardGiven = true
        goto(EndState)
    }
    onButton(redCardButton){
        cardGiven = true
        goto(EndState)
    }
}

val EndState = state(Interaction) {
    onEntry {
        when (politenessScore) {
            0 -> {
                furhat.gesture(Gestures.BigSmile(duration=5.0))
                furhat.say("Let's continue the game and respect each other.<break time=\"3s\"/>")
            }
            1 -> {
                furhat.gesture(Gestures.GazeAway(duration=1.0))
                furhat.say("Well, let's continue this game.<break time=\"3s\"/>")
            }
            2 -> {
                furhat.gesture(Gestures.ExpressDisgust(duration=5.0))
                furhat.say("Let's continue the game, i am wasting my time on you.<break time=\"3s\"/>")
            }
            else -> {
                furhat.gesture(Gestures.ExpressAnger(duration=5.0))
                furhat.say("Let's continue this game, loser.<break time=\"3s\"/>")
            }
        }
        furhat.gesture(Gestures.Smile)
        furhat.setTexture("Ivan")
        furhat.voice = PollyVoice.Brian()
        furhat.say("You have reached the end of this referee training session. Thank you for your participation.")
        furhat.gesture(Gestures.BigSmile)
    }
}