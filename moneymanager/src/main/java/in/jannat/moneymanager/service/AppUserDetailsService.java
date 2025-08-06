package in.jannat.moneymanager.service;
import in.jannat.moneymanager.entity.ProfileEntity;
import in.jannat.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;

//    Responsible for loading the profile from the database.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
           ProfileEntity existingProfile= profileRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("No profile found with email "+email));
           return User.builder()
                   .username(existingProfile.getEmail())
                   .password(existingProfile.getPassword())
                   .authorities(Collections.emptyList())
                   .build();

    }
}
//     return User.builder()
//    .username(existingProfile.getEmail()) // used for auth context
//    .password(existingProfile.getPassword()) // used for password matching
//    .authorities(Collections.emptyList()) // no roles/authorities (can be added later)
//    .build();