package com.example.fase1_grupob;


import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;



@Controller
public class UserController 
{
    @GetMapping("/user")
    public String user()
    {
        return "user_template";
    }
}
