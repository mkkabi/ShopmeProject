package com.shopme.admin.user;

import com.shopme.admin.utils.FileUploadUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    private static final String uploadDir = "ShopmeWebParent/ShopmeBackEnd/user-photos";
//    private static final String uploadDir = "user-photos";
    @Autowired
    private UserServie userService;

    @GetMapping("/allUsers")
    public String listAll(Model model){
        List<User> users = userService.ListAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users")
    public String showFirstPage(Model model){
        listByPage(1, "firstName", "asc", null, model);
        return "users";
    }

    @GetMapping("/users/page/{pageNumber}")
        public String listByPage(@PathVariable(name="pageNumber")int pageNum, @Param("sortField") String sortField, @Param("sortDir")String sortDir,@Param("keyword")String keyword, Model model){

            Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
            List<User> listUsers = page.getContent();
            long totalElements = page.getTotalElements();
            int totalPages = page.getTotalPages();
            long startCount = (pageNum-1)*UserServie.USERS_PER_PAGE+1;
            long endCount = startCount+UserServie.USERS_PER_PAGE-1;
            String reverseSortDir = sortDir.equals("acs")?"desc":"asc";

            if(endCount>page.getTotalElements()){
                endCount=page.getTotalElements();
            }
//        System.out.println("page Number = " + pageNum);
//        System.out.println("Total elemtnts = "+ totalElements);
//        System.out.println("total pages = "+ totalPages);
            model.addAttribute("users", listUsers);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("sortField", sortField);
            model.addAttribute("keyword", keyword);
        String sortValueSwitcher = sortDir.equals("asc")?"desc":"asc";
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", reverseSortDir);
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
        String message="";
        User savedUser = null;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if(!multipartFile.isEmpty()){
            user.setPhotos(fileName);
            savedUser = userService.save(user);
            FileUploadUtil.clean(uploadDir+"/"+user.getId());
            FileUploadUtil.saveFile(uploadDir+"/"+user.getId(), fileName, multipartFile);
        }else {
            if(user.getPhotos()!=null && user.getPhotos().isEmpty()) user.setPhotos(null);
            savedUser = userService.save(user);
        }
        message = "User " +savedUser.getEmail()+ " has been saved successfully. ID: "+savedUser.getId();
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users/page/1?sortField=email&sortDir=asc&keyword="+user.getEmail();
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

    @GetMapping("/users/export/csv")
    public void exporToCSV(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String message = "";
        List<User> userList = userService.ListAll();
//        try {
            UserCsvExporter exporter = new UserCsvExporter();
            exporter.export(userList, response);
//        }catch (IOException e){
//            message = "Error exporting file "+e.getMessage();
//        }catch(IllegalArgumentException i){
//            message = "Error exporting file "+i.getMessage();
//        }
        redirectAttributes.addFlashAttribute("message", message);
//        return "redirect:/users";
    }
























}
