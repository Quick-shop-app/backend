package ugr.dss.quick_shop.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.Product;
import ugr.dss.quick_shop.services.ProductsRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import ugr.dss.quick_shop.models.AppUser;
import ugr.dss.quick_shop.models.RegisterDto;
import ugr.dss.quick_shop.repositories.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
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

}
