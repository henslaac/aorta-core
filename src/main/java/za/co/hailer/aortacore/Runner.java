package za.co.hailer.aortacore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    public static void main(String[] args){
        Pattern pattern = Pattern.compile("(sentence|is)\\s*(\\w+)");
        Matcher matcher = pattern.matcher("some lame sentence that is awesome");
        boolean found = false;
        while (matcher.find()) {
            System.out.println("I found the text: " + matcher.group(2).trim());
            found = true;
        }
        if (!found) {
            System.out.println("I didn't find the text ");
        }
    }
}
