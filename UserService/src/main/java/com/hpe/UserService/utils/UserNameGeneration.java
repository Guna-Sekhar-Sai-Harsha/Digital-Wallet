package com.hpe.UserService.utils;

public class UserNameGeneration {
    /*
        Username = FirstName[0] + LastName + 3 Digit Random Number
     */

    public static String genUserName(String firstName, String lastName){

        return firstName.charAt(0)+lastName+String.valueOf(Math.round(100 * Math.random()));
    }

}
