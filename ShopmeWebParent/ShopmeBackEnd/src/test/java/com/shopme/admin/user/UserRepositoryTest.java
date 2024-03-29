package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureMockMvc
//@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole(){
        Role admin = entityManager.find(Role.class, 2);
        User user = new User("name@codejava.net", "name2020", "John", "Doe");
        user.addRole(admin);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRoles(){
        User user = new User("user1@gmail.com", "user2022", "Ravi", "Kumar");
        Role editor = entityManager.find(Role.class, 6);
        Role assistant = entityManager.find(Role.class, 8);
        user.addRole(editor);
        user.addRole(assistant);

        User saved = userRepository.save(user);
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void testAllUsers(){
        Iterable<User> users = userRepository.findAll();
        List<User> usersList = new ArrayList<>();
        users.forEach(user-> System.out.println(user));
        users.forEach(usersList::add);
        assertThat(usersList.size()).isGreaterThan(0);
    }

    @Test
    public void testGetUserById(){
        User user = userRepository.findById(8).get();
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user = userRepository.findById(9).get();
        user.setEnabled(true);
        String newEmail = "newemail@gmail.com";
        user.setEmail(newEmail);
        userRepository.save(user);
        User found = entityManager.find(User.class, 9);
        assertThat(found.getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void testUpdateUserRole(){

        User user = userRepository.findById(9).get();
        Role editor = new Role(6);
        Role salesPerson = new Role(5);
        user.getRoles().remove(editor);
        user.getRoles().add(salesPerson);
        userRepository.save(user);

        assertThat(userRepository.findById(9).get().getRoles()).contains(new Role(5));
    }

    @Test
    public void testDeleteUser(){
        Integer userId = 9;
//        userRepository.deleteById(9);


        Exception exception = assertThrows(EmptyResultDataAccessException.class, () -> {
            userRepository.findById(9).get();
        });

        String expectedMessage = "User entity with id 9 exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetUserByEmail(){
        String email = "abc@def.com";
        User user = userRepository.getUserByEmail(email);
        assertThat(user).isNull();
        User user2 = userRepository.getUserByEmail("spencer@grr.la");
        assertThat(user2).isNotNull();
    }

    @Test
    public void testCountById(){
        Integer id = 17;
        Long countById = userRepository.countById(id);
        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser(){
        Integer id = 30;
        userRepository.updateEnabledStatus(id, false);
    }

    @Test
    public void testEnableUser(){
        Integer id = 30;
        userRepository.updateEnabledStatus(id, true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);

        List<User> userList = page.getContent();
        userList.forEach(System.out::println);

        assertThat(userList.size()).isEqualTo(4);
    }
    @Test
    public void testSearchUser(){
        String keyword = "bruce";
        int pageNumber =0;
        int pageSize =4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(keyword, pageable);
        List<User> userList = page.getContent();

        userList.forEach(u->{
            StringBuffer sb = new StringBuffer();
            sb.append(u.getFirstName()).append(u.getLastName()).append(u.getEmail());
            assertThat(sb.toString().toLowerCase()).contains(keyword);
        });
        System.out.println("Users search from test: ===========");
        userList.forEach(System.out::println);
    }
}
