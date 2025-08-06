package in.jannat.moneymanager.repository;

import in.jannat.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
//    select * from tbl_categories where profile_id=?1
    List<CategoryEntity> findByProfileId(Long profileId);

//    select * from tbl_categories where id=?1 && profile_id=?1
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

   List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

   Boolean existsByNameAndProfileId(String name, Long profileId);


}

//This interface helps you query the categories table based on user (profile), type, ID, or name — all without writing SQL manually. Spring Data JPA automatically generates the queries based on method names.