package com.verbole.dcad.tabula;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String ess = "%\\sansqu{1 saeptus}";
        String expected = "1 saeptus";
        String actual = regexTest(ess); //upperBoundSearchString(ess);

        assertEquals(expected, actual);
    }

    String upperBoundSearchString(String text) {

        int length = text.length();
        String baseString = "";
        String incrementedString = "";

        if (length < 1) {
            return text;
        }
        else if (length > 1) {
            baseString = text.substring(0,text.length() - 1);
        } else {
            baseString = "";
        }

        String dernChar = text.substring(text.length() - 1,text.length());

        Character lastChar = dernChar.charAt(0);

        int incrementedChar = -1;

        //https://developer.apple.com/library/content/samplecode/DerivedProperty/Listings/DerivedProperty_APLNormalizedStringTransformer_m.html
        // Don't do a simple lastChar + 1 operation here without taking into account
        // unicode surrogate characters (http://unicode.org/faq/utf_bom.html#34).

        if ((lastChar >= 0xD800) && (lastChar <= 0xDBFF)) {         // surrogate high character
            incrementedChar = (0xDBFF + 1);
        } else if ((lastChar >= 0xDC00) && (lastChar <= 0xDFFF)) {  // surrogate low character
            incrementedChar = (0xDFFF + 1);
        } else if (lastChar == 0xFFFF) {
            if (length > 1 ) {
                baseString = text;
                incrementedChar =  0x1;
            }
        } else {
            incrementedChar = lastChar + 1;
        }

        incrementedString = String.format("%s%c", baseString, incrementedChar);
        // print("incremented string : " + incrementedString)
        //incrementedString = String(format:"%C", incrementedChar)
        return incrementedString;
    }

    String regexTest(String test) {
        String pat = "%*\\\\sansqu\\{([a-zA-Z0-9 ]+)\\}";

        Pattern patternEntr = Pattern.compile(pat);
        Matcher matcher = patternEntr.matcher(test);
        String res = "";
        if (matcher.matches()) {
            res = matcher.group(1);
        }
        else {
            res = test + "yo";
        }

        //res = test.replaceAll(pat,"$1");

        return res;
    }


}