package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserServie userService;

    @GetMapping("/users")
    public String listAll(Model model){
        List<User> users = userService.ListAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
    	User user = new User();
    	user.setEnabled(true);
    	List<Role> roles = userService.listRoles();
         model.addAttribute("title", "Create new User");
         model.addAttribute("user", user);
         model.addAttribute("roles", roles);
        return "user_form";
    }
    
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
    	userService.save(user);
    	String message = "User " +user.getEmail()+ " has been saved successfully. ID: "+user.getId();
    	redirectAttributes.addFlashAttribute("message", message);
    	return "redirect:/users";
    }
}
