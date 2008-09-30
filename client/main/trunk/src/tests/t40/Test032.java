package tests.t40;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory;

public class Test032
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        KevinVoiceDirectory dir = new KevinVoiceDirectory();
        Voice voice = dir.getVoices()[1];
        System.out.println("allocating");
        voice.allocate();
        System.out.println("allocated");
        voice.setRate(130);
        voice.setPitch(100);
        voice.setPitchRange(25);
        voice.speak("Hello. I am a talking machine. I can talk forever and ever without getting tired.");
    }
    
}
