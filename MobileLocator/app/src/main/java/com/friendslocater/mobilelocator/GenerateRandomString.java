package com.friendslocater.mobilelocator;
import java.util.Random;

/**
 * Created by Anandhb on 23-09-2017.
 */
    public class GenerateRandomString {

        public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static Random RANDOM = new Random();

        public static String randomString(int len) {
            StringBuilder sb = new StringBuilder(len);

            for (int i = 0; i < len; i++) {
                sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
            }

            return sb.toString();
        }
    }


    /*protected String createRandomRegistryId(String handleId)
    {
        // syntax we would like to generate is DIA123456-A1B34
        String val = "DI";

        // char (1), random A-Z
        int ranChar = 65 + (new Random()).nextInt(90-65);
        char ch = (char)ranChar;
        val += ch;

        // numbers (6), random 0-9
        Random r = new Random();
        int numbers = 100000 + (int)(r.nextFloat() * 899900);
        val += String.valueOf(numbers);

        val += "-";
        // char or numbers (5), random 0-9 A-Z
        for(int i = 0; i<6;){
            int ranAny = 48 + (new Random()).nextInt(90-65);

            if(!(57 < ranAny && ranAny<= 65)){
                char c = (char)ranAny;
                val += c;
                i++;
            }

        }

        return val;
    }*/

