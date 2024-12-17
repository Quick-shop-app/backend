package ugr.dss.quick_shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ugr.dss.quick_shop.models.auth.AppUser;
import ugr.dss.quick_shop.repositories.AppUserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Seed the database with an admin user on startup
     */
    @Override
    public void run(String... args) {
        // Create an admin user if it doesn't exist
        if (userRepository.findByEmail("admin@admin.com") == null) {
            AppUser admin = new AppUser();
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
}