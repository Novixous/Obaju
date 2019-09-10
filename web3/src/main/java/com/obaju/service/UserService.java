package com.obaju.service;

import com.obaju.model.Role;
import com.obaju.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class UserService implements UserDetailsService {


    @PersistenceContext
    EntityManager entityManager;

    public void create(User user) {
        entityManager.persist(user);//insert or update
    }

    public User getUserEmail(String email) {
        List<User> list = entityManager.createQuery("from User where email = :email")
                .setParameter("email", email).getResultList();
        if (list.size() == 1) return list.get(0);
        return null;
    }

    public Role getRole(String roleName) {
        List<Role> list = entityManager.createQuery("from Role " +
                "where name = :roleName")
                .setParameter("roleName", roleName).getResultList();
        if (list.size() == 1) return list.get(0);
        return null;
    }

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return getUserEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserEmail(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        return user;
    }
}
