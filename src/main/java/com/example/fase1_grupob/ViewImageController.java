package com.example.fase1_grupob;


import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;



@Controller
public class ViewImageController 
{
    @GetMapping("/viewimage")
    public String viewimage()
    {
        return "viewImage_template";
    }
}
