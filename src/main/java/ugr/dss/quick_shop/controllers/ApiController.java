package ugr.dss.quick_shop.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.services.ProductsRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import ugr.dss.quick_shop.models.AppUser;
import ugr.dss.quick_shop.models.LoginDto;
import ugr.dss.quick_shop.models.RegisterDto;
import ugr.dss.quick_shop.repositories.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping("/products")
    public HashMap<String, Object> products() {
        HashMap<String, Object> response = new HashMap<>();
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        response.put("count", products.size());
        response.put("data", products);
        return response;
    }

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        AppUser existingUser = appUserRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            result.addError(new FieldError("registerDto", "email", "Email already in use"));
            return "register";
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Passwords do not match"));
            return "register";
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

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);

            return "redirect:/login";
        } catch (Exception e) {
            result.addError(new FieldError("registerDto", "email", "An error occurred while processing your request"));
            return "register";
        }
    }

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody @Valid LoginDto loginDto, BindingResult result) {
        System.out.println("🚀 ~ " + loginDto);
        HashMap<String, Object> response = new HashMap<>();

        // Check for validation errors
        if (result.hasErrors()) {
            response.put("error", "Wrong email or password");
            return response;
        }

        // Check if user exists
        AppUser user = appUserRepository.findByEmail(loginDto.getEmail());
        System.out.println(user);

        if (user == null) {
            result.addError(new FieldError("loginDto", "email", "Wrong email or password"));
            // model.addAttribute("error", "Wrong email or password");
            response.put("error", "Wrong email or password");
            return response;
        }

        // Check if password is correct
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            result.addError(new FieldError("loginDto", "email", "Wrong email or password"));
            // model.addAttribute("error", "Wrong email or password");
            response.put("error", "Wrong email or password");
            return response;
        }

        System.out.println("User logged in");

        try {
            response.put("success", true);

            return response;
        } catch (Exception e) {
            result.addError(new FieldError("loginDto", "email", "An error occurred while processing your request"));
            response.put("error", "An error occurred while processing your request");
            return response;
        }

    }

}
