package com.adi.todo;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> namesList = List.of("abc","pqr,asd","qwer","abc");

        String nameTOCheck = "abc";

        String presetNameOrNot = namesList.stream().filter(name->name.equals(nameTOCheck)).findAny().orElse("not present");

        System.out.println(presetNameOrNot);
    }

}
