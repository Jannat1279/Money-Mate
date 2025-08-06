package in.jannat.moneymanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="tbl_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type;
    private String icon;
    @ManyToOne(fetch = FetchType.LAZY)
//    fetch = FetchType.LAZY: The associated ProfileEntity data will only be loaded when explicitly accessed (not immediately when the category is fetched). This improves performance by avoiding unnecessary data loading.
    @JoinColumn(name="profile_id", nullable = false)
    private ProfileEntity profile;
}
//The CategoryEntity class is a JPA entity that maps to the tbl_categories table in the database and represents a financial category, such as "Food" or "Rent", in a money management application. Each category has an id, a name, a type(like income or expense), and timestamps to track when it was created and last updated. The createdAt field is automatically set when the category is first saved. This entity also has a many-to-one relationship with a ProfileEntity, meaning each category belongs to a specific user profile.
