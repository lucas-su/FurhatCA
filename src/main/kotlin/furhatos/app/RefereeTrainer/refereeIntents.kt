package furhatos.app.gettingstarted2
import furhatos.nlu.Intent
import furhatos.util.Language

class yellowCard : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you deserve +yellow", "the +yellow card", "+yellow", "+a +yellow card", "Yellowcard")
    }
}

class redCard : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you deserve +red","a +red card", "+red")
    }
}

class Freekick : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you are getting a +free +kick", "a free kick", "freekick")
    }
}

class Foul : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("you +hit the player", "you did something wrong", "you committed a +foul",
                "you were a bad boy", "you tackled someone", "+foul", "+violation", "+tackle",
                "hit the player", "you +kicked someone", "you +kicked a player", "on +purpose")
    }
}

class giveCard : Intent(){
    override fun getExamples(lang: Language): List<String>{
        return listOf( "I give you this", "here is your card", "this is for you", "that is my final +decision")
    }
}

//class beingPolite : Intent(){
//    override fun getExamples(lang: Language): List<String>{
//        return listOf( "please", "calm down", "let's discuss this", "I don't want to make you angry", "don't be angry please", "stay calm", "I have to follow the rules")
//    }
//}
//
//class beingRude : Intent(){
//    override fun getExamples(lang: Language): List<String>{
//        return listOf("die", "I want you gone", "corona", "piss off", "dick", "asshole", "mother", "go away", "out of the field", "sick", "stupid", "blind", "weak", "ugly", "manners", "impolite")
//    }
//}

class disagreeWithPlayer : Intent(){
    override fun getExamples(lang: Language): List<String>{
        return listOf("I +disagree", "I +disagree with you","That is not true", "untrue", "I doubt that", "I doubt what you are saying", "I think that you are lying", "you are lying", "lie", "That is a lie")
    }
}

class agreeWithPlayer : Intent(){
    override fun getExamples(lang: Language): List<String>{
        return listOf("I +agree","I +agree with you", "That is true", "true", "correct", "I understand you", "We understand each other", "you are right")
    }
}