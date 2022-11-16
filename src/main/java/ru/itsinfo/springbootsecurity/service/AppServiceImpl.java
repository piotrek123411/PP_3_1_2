package ru.itsinfo.springbootsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ru.itsinfo.springbootsecurity.model.Role;
import ru.itsinfo.springbootsecurity.model.User;
import ru.itsinfo.springbootsecurity.repository.RoleRepository;
import ru.itsinfo.springbootsecurity.repository.UserRepository;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppServiceImpl implements AppService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AppServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", email))
        );
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName", "lastName"));
    }

    @Override
    public User findUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean saveUser(User user, BindingResult bindingResult, Model model) {
        model.addAttribute("allRoles", findAllRoles());

        if (bindingResult.hasErrors()) {
            return false;
        }
        user.setPassword(user.getPassword());

        try {
            userRepository.save(user);
        } catch (PersistenceException e) {
            model.addAttribute("persistenceException", true);
            return false;
        }
        return true;
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            try {
                userRepository.delete(user.get());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }
}
