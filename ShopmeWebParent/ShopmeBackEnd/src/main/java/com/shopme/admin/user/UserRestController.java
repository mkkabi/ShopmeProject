package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class UserRestController {
    @Autowired
    private UserServie servie;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id")Integer id, @Param("email") String email){
        return servie.isEmailUnique(id, email)?"OK":"Duplicated";
    }


}
