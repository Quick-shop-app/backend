package ugr.dss.quick_shop.controllers.api;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ugr.dss.quick_shop.models.LoginDto;
import ugr.dss.quick_shop.models.RegisterDto;
import ugr.dss.quick_shop.services.AppUserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AccountApiController {

    @Autowired
    private AppUserService appUserService;

    /**
     * Register a new user
     * 
     * @param registerDto
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/register")
    public HashMap<String, Object> registerUser(@Valid @RequestBody RegisterDto registerDto,
            Model model) {
        return appUserService.register(registerDto);
    }

    /**
     * Login user
     * 
     * @param loginDto
     * @param result
     * @return
     */
    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody @Valid LoginDto loginDto) {
        return appUserService.login(loginDto.getEmail(), loginDto.getPassword());
    }

}
