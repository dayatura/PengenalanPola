package pengenalanpola.if5181.if5181pengenalanpola;

import android.util.Log;

import java.util.ArrayList;

public class ChainCodeUtil {

    public static int translate(String chain) {

        ArrayList<Integer> count;
        String simpleChain = getSimplifiedChain(chain);

        if (simpleChain.equals("2460")) {
            count = getCountChain(chain);

            if((double)count.get(0) / count.get(1) <= 0.2){
                return 1;
            }
            else{
                return 0;
            }
        } else if (simpleChain.equals("246424602060")) {
            count = getCountChain(chain);

            if (count.get(1) > count.get(count.size() - 3)) {
                return 2;
            } else {
                return 5;
            }
        } else if (simpleChain.equals("246020602060")) {
            return 3;
        } else if (simpleChain.equals("2420246060")) {
            return 4;
        } else if (simpleChain.equals("24642460")) {
            return 6;
        } else if (simpleChain.equals("246060")) {
            return 7;
        } else if (simpleChain.equals("24602060")) {
            return 9;
        } else {
            return -1;
        }
    }

    public static String getSimplifiedChain(String chain) {

        if (chain.length() < 2) {
            return chain;
        }

        char current;
        char last = chain.charAt(0);
        StringBuilder result = new StringBuilder();

        result.append(last);

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (current != last && Character.getNumericValue(current) % 2 == 0) {
                last = chain.charAt(i);
                result.append(last);
            }
        }

        return result.toString();
    }

    public static ArrayList<Integer> getCountChain(String chain) {

        if (chain.length() < 2) {
            return new ArrayList<>();
        }

        ArrayList<Integer> list = new ArrayList<>();
        char current;
        char last = chain.charAt(0);
        int counter = 1;

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (Character.getNumericValue(current) % 2 == 0) {
                if (current != last) {
                    list.add(counter);
                    last = chain.charAt(i);
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }

        list.add(counter);

        return list;
    }
}
