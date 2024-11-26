package ugr.dss.quick_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ugr.dss.quick_shop.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    public AppUser findByEmail(String email);

}
