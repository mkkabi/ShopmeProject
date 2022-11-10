package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends PagingAndSortingRepository<User, Integer>, CrudRepository<User, Integer> {

    @Query("Select u from User u where u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);

    @Query("UPDATE User u SET u.enabled = ?2 where u.id =?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);
    @Query("Select u FROM User u WHERE concat(u.firstName, ' ', u.lastName, ' ', u.email ) like %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);

}
