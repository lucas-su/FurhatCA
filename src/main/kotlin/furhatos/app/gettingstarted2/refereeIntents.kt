package furhatos.app.gettingstarted2

import furhatos.nlu.Intent
import furhatos.util.Language



class yellowCard : Intent() {

    override fun getExamples(lang: Language): List<String> {
        return listOf("you deserve +yellow", "the +yellow card", "+yellow", "+a +yellow card")
    }
}

class redCard : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you deserve +red","a +red card", "+red")
    }
}

class warning : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you are getting a +warning", "you're getting +warning", "a warning", "i am giving you a +warning", "nothing")
    }
}

class Foul : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you did something wrong", "you committed a +foul", "you were a bad boy", "you tackled someone", "+foul", "+violation", "+tackle", "hit the player")
    }
}

class giveCard : Intent(){
    override fun getExamples(lang: Language): List<String>{
        //cardGiven = true;
        return listOf( "I give you this", "here is your card", "this is for you")
    }
}

class beingPolite : Intent(){
    override fun getExamples(lang: Language): List<String>{
        return listOf( "please", "calm down please", "let's discuss this", "I don't want to make you angry", "don't be angry please", "stay calm", "I have to follow the rules")
    }
}

class beingRude : Intent(){
    override fun getExamples(lang: Language): List<String>{
        return listOf("die", "I want you gone", "corona", "piss off", "dick", "asshole", "mother", "go away", "out of the field", "sick", "stupid", "blind", "weak", "ugly", "manners", "impolite")
    }
}