package ru.hse.smartrefrigerator.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateParse {

    static String[] numbers = {
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten",
            "eleven",
            "twelve",
            "thirteen",
            "twenty",
            "thirty",
            "forty",
            "fifty",
    };


    static String[] timeUnits = {
            "day",
            "days",
            "week",
            "weeks",
            "month",
            "months"
    };


    public static String[] parseVoiceString(String text) {
        String fullText = text;
        HashMap<String, Integer> stringToInteger = new HashMap<String, Integer>();
        stringToInteger.put("one", 1);
        stringToInteger.put("two", 2);
        stringToInteger.put("to", 2);
        stringToInteger.put("too", 2);
        stringToInteger.put("who", 2);
        stringToInteger.put("three", 3);
        stringToInteger.put("four", 4);
        stringToInteger.put("five", 5);
        stringToInteger.put("six", 6);
        stringToInteger.put("seven", 7);
        stringToInteger.put("eight", 8);
        stringToInteger.put("nine", 9);
        stringToInteger.put("ten", 10);
        stringToInteger.put("eleven", 11);
        stringToInteger.put("twelve", 12);
        stringToInteger.put("thirteen", 13);
        stringToInteger.put("twenty", 20);
        stringToInteger.put("thirty", 30);
        stringToInteger.put("forty", 40);
        stringToInteger.put("fifty", 50);


        HashMap<String, Integer> dayToInteger = new HashMap<String, Integer>();
        dayToInteger.put("day", 1);
        dayToInteger.put("days", 1);
        dayToInteger.put("babies", 1);
        dayToInteger.put("x.", 7);
        dayToInteger.put("week", 7);
        dayToInteger.put("weeks", 7);
        dayToInteger.put("reeks", 7);
        dayToInteger.put("reek", 7);
        dayToInteger.put("month", 30);
        dayToInteger.put("months", 30);
        dayToInteger.put("years", 360);
        dayToInteger.put("year", 360);


        ArrayList<Integer> allNumbers = new ArrayList<Integer>();
        int index = -1;
        int number;
        for (String temp : numbers) {
            index = text.indexOf(temp);
            if (index > -1) {
                number = stringToInteger.get(temp);
                if (index + temp.length() < text.length()) {
                    if (text.charAt(index + temp.length()) == ' ') {
                        text = text.replace((text.substring(index, index + temp.length())), "");
                        allNumbers.add(number);

                    } else {
                        String we = text.substring(index + temp.length(), index + temp.length() + 4);
                        if (text.substring(index + temp.length(), index + temp.length() + 4).equals("teen")) {
                            number += 10;
                            text = text.replace((text.substring(index, index + temp.length() + 4)), "");
                            allNumbers.add(number);

                        }
                        if (text.substring(index + temp.length(), index + temp.length() + 2).equals("ty")) {
                            number *= 10;
                            text = text.replace((text.substring(index, index + temp.length() + 2)), "");
                            allNumbers.add(number);

                        }

                    }
                }
            }
        }

        int numberOfUnits = 0;
        for (Integer a : allNumbers) {
            numberOfUnits += a;
        }

        int digitUnit = 1;
        for (String unit : timeUnits) {
            int tempIndex = text.indexOf(unit);
            if (text.contains(unit)) {
                if (tempIndex + unit.length() < text.length()) {
                    digitUnit = dayToInteger.get(unit);
                    text = text.replace((text.substring(tempIndex, tempIndex + unit.length() + 1)), "");
                    break;
                } else {
                    digitUnit = dayToInteger.get(unit);
                    text = text.replace(unit, "");
                    break;
                }
            }
        }


        int res = digitUnit * numberOfUnits;
        String nameOfProduct = text.trim();
        text = fullText.trim();
        String otherText = text.replace(nameOfProduct, "").trim();

        System.out.println(nameOfProduct);
        System.out.println(otherText);
        System.out.println(res);
        String[] results = {nameOfProduct, otherText};
        return results;
    }

    public static Date dateFromString(String text) {
        HashMap<String, Integer> stringToInteger = new HashMap<String, Integer>();
        stringToInteger.put("one", 1);
        stringToInteger.put("two", 2);
        stringToInteger.put("three", 3);
        stringToInteger.put("four", 4);
        stringToInteger.put("five", 5);
        stringToInteger.put("six", 6);
        stringToInteger.put("seven", 7);
        stringToInteger.put("eight", 8);
        stringToInteger.put("nine", 9);
        stringToInteger.put("ten", 10);
        stringToInteger.put("eleven", 11);
        stringToInteger.put("twelve", 12);
        stringToInteger.put("thirteen", 13);
        stringToInteger.put("twenty", 20);
        stringToInteger.put("thirty", 30);
        stringToInteger.put("forty", 40);
        stringToInteger.put("fifty", 50);

        ArrayList<Integer> allNumbers = new ArrayList<Integer>();
        int index = -1;
        int number;
        for (String temp : numbers) {
            index = text.indexOf(temp);
            if (index > -1) {
                number = stringToInteger.get(temp);
                if (index + temp.length() < text.length()) {
                    if (text.charAt(index + temp.length()) == ' ') {
                        text = text.replace((text.substring(index, index + temp.length())), "");
                        allNumbers.add(number);

                    } else {
                        String we = text.substring(index + temp.length(), index + temp.length() + 4);
                        if (text.substring(index + temp.length(), index + temp.length() + 4).equals("teen")) {
                            number += 10;
                            text = text.replace((text.substring(index, index + temp.length() + 4)), "");
                            allNumbers.add(number);

                        }
                        if (text.substring(index + temp.length(), index + temp.length() + 2).equals("ty")) {
                            number *= 10;
                            text = text.replace((text.substring(index, index + temp.length() + 2)), "");
                            allNumbers.add(number);

                        }

                    }
                }
            } else {
                continue;
            }


        }

        int numberOfUnits = 0;
        for (Integer a : allNumbers) {
            numberOfUnits += a;
        }

        HashMap<String, Integer> dayToInteger = new HashMap<String, Integer>();
        dayToInteger.put("day", 1);
        dayToInteger.put("days", 1);
        dayToInteger.put("week", 7);
        dayToInteger.put("weeks", 7);
        dayToInteger.put("month", 30);
        dayToInteger.put("months", 30);
        dayToInteger.put("Day", 1);
        dayToInteger.put("Days", 1);
        dayToInteger.put("Week", 7);
        dayToInteger.put("Weeks", 7);
        dayToInteger.put("Month", 30);
        dayToInteger.put("Months", 30);

        int digitUnit = 1;
        for (String unit : timeUnits) {
            int tempIndex = text.indexOf(unit);
            if (text.contains(unit)) {
                if (tempIndex + unit.length() < text.length()) {
                    digitUnit = dayToInteger.get(unit);
                    text = text.replace((text.substring(tempIndex, tempIndex + unit.length() + 1)), "");
                    break;
                } else {
                    digitUnit = dayToInteger.get(unit);
                    text = text.replace(unit, "");
                    break;
                }
            }
        }

        int res = digitUnit * numberOfUnits;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, res);  // number of days to add
        return c.getTime();
    }

    public static String parseTranscript(String s) {
        int i = s.indexOf("transcript");
        if (i > 0) {
            s = s.substring(i + 14);
            return s.substring(0, s.indexOf('\"'));
        } else {
            return "";
        }
    }
}
