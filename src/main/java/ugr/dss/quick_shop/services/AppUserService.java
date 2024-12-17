package ugr.dss.quick_shop.services;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ugr.dss.quick_shop.models.AppUser;
import ugr.dss.quick_shop.models.RegisterDto;
import ugr.dss.quick_shop.repositories.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Load user by username, used by Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDetails springUser = User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();

        return springUser;
    }

    /**
     * Register a new user
     * 
     * @param registerDto
     * @return
     */
    public HashMap<String, Object> register(RegisterDto registerDto) {
        HashMap<String, Object> response = new HashMap<>();

        AppUser existingUser = appUserRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            response.put("error", "Email already in use");
            return response;
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            response.put("error", "Passwords do not match");
            return response;
        }

        try {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            AppUser user = new AppUser();
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setEmail(registerDto.getEmail());
            user.setPhone(registerDto.getPhone());
            user.setAddress(registerDto.getAddress());
            user.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
            user.setRole("USER");
            user.setCreatedAt(new java.util.Date());

            appUserRepository.save(user);

            response.put("success", true);
            response.put("message", "User registered successfully");
            return response;
        } catch (Exception e) {
            response.put("error", "An error occurred while processing your request");
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * Login user
     * 
     * @param email
     * @param password
     * @return
     */
    public HashMap<String, Object> login(String email, String password) {
        HashMap<String, Object> response = new HashMap<>();

        // Check if user exists
        AppUser user = appUserRepository.findByEmail(email);
        System.out.println(user);

        if (user == null) {
            response.put("error", "Wrong email or password");
            return response;
        }

        // Check if password is correct
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            response.put("error", "Wrong email or password");
            return response;
        }

        try {
            response.put("success", true);
            // add user details to response without password
            user.setPassword(null);
            response.put("data", user);

            return response;
        } catch (Exception e) {
            response.put("error", "An error occurred while processing your request");
            return response;
        }

    }

}
