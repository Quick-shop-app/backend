package ugr.dss.quick_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.AppUser;
import ugr.dss.quick_shop.models.LoginDto;
import ugr.dss.quick_shop.models.RegisterDto;
import ugr.dss.quick_shop.repositories.AppUserRepository;

@Controller
public class AccountController {
    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success", false);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result,
            Model model) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "register";
        }

        // Check if email already exists
        AppUser existingUser = appUserRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            result.addError(new FieldError("registerDto", "email", "Email already in use"));
            return "register";
        }

        // Check if passwords match
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Passwords do not match"));
            return "register";
        }

        // Proceed with user registration
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

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);

            return "redirect:/login";
        } catch (Exception e) {
            result.addError(new FieldError("registerDto", "email", "An error occurred while processing your request"));
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        LoginDto loginDto = new LoginDto();
        model.addAttribute(loginDto);
        model.addAttribute("success", false);
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDto") LoginDto loginDto, BindingResult result, Model model) {
        System.out.println("🚀 ~ " + loginDto);

        // Check for validation errors
        if (result.hasErrors()) {
            return "login";
        }

        // Check if user exists
        AppUser user = appUserRepository.findByEmail(loginDto.getEmail());
        System.out.println(user);

        if (user == null) {
            result.addError(new FieldError("loginDto", "email", "Wrong email or password"));
            model.addAttribute("error", "Wrong email or password");
            return "login";
        }

        // Check if password is correct
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            result.addError(new FieldError("loginDto", "email", "Wrong email or password"));
            model.addAttribute("error", "Wrong email or password");
            return "login";
        }

        System.out.println("User logged in");

        // Proceed with user login
        try {
            model.addAttribute("loginDto", new LoginDto());
            model.addAttribute("success", true);

            return "redirect:/";
        } catch (Exception e) {
            result.addError(new FieldError("loginDto", "email", "An error occurred while processing your request"));
            return "login";
        }
    }
}
