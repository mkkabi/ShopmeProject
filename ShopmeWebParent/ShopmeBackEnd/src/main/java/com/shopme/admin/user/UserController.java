package com.shopme.admin.user;

import com.shopme.admin.utils.FileUploadUtil;
import org.springframework.util.StringUtils;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "ShopmeWebParent/ShopmeBackEnd/user-photos";
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);


    	userService.save(user);
    	String message = "User " +user.getEmail()+ " has been saved successfully. ID: "+user.getId();
    	redirectAttributes.addFlashAttribute("message", message);
    	return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name="id") Integer id,
                           Model model, RedirectAttributes redirectAttributes){
        try {
            User user = userService.get(id);
            model.addAttribute("title", "Edit user "+user.getId());
            model.addAttribute("user", user);
            model.addAttribute("roles", userService.listRoles());
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(("id"))Integer id, RedirectAttributes redirectAttributes){
        String message = "User removed, ID = "+id;
        try {
            userService.deleteUser(id);
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id")Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        userService.updateUserEnableStatus(id, enabled);
        String status = enabled?"enabled":"disabled";
        String message = "The user "+id+" has been "+status;

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }
























}
