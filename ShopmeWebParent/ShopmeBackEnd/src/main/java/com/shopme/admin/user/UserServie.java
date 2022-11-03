package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServie {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void encodePassword(User user){
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
    }

    public List<User> ListAll(){
        return (List<User>) userRepository.findAll();
    }
    
    public List<Role> listRoles(){
    	return (List<Role>)roleRepository.findAll();
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        }catch (NoSuchElementException e){
            throw new UserNotFoundException("Could not find any user with ID "+id);
        }
        }
	public void save(User user) {
        boolean isUpdatingUser = (user.getId() !=null);
        if(isUpdatingUser){
            User existingUser = userRepository.findById(user.getId()).get();
            // checking if password was submitted into the password field, if yes - leaving it same as original
            // if field is submitted, encrypting and saving new pass
            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }else{
                encodePassword(user);
            }

        }else{
            encodePassword(user);
        }

		userRepository.save(user);
	}

    public boolean isEmailUnique(Integer id, String email){
        User user = userRepository.getUserByEmail(email);
        if(user==null) return true;
        boolean creatingNewUser = (id == null);
        if(creatingNewUser) {
            System.out.println("creating new user");
            return false;
        }else{
            System.out.println("editing user "+id);
            return user.getId().equals(id);
        }
    }

    public void deleteUser(Integer id) throws UserNotFoundException{
        Long countById = userRepository.countById(id);
        if(countById==null || countById ==0){
            throw new UserNotFoundException("Could not find user with this ID "+id);
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnableStatus(Integer id, boolean enabled){
        userRepository.updateEnabledStatus(id, enabled);
    }
}
