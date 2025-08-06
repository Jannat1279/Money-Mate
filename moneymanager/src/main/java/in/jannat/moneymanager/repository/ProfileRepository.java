package in.jannat.moneymanager.repository;

import in.jannat.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long>{
//    select * from tbl_profiles where email=?1
    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
