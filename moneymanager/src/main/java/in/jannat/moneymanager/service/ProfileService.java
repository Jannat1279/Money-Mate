package in.jannat.moneymanager.service;

import in.jannat.moneymanager.dto.AuthDTO;
import in.jannat.moneymanager.dto.ProfileDTO;
import in.jannat.moneymanager.entity.ProfileEntity;
import in.jannat.moneymanager.repository.ProfileRepository;
import in.jannat.moneymanager.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfile=toEntity(profileDTO);
//        UUID.randomUUID().toString() generates a random unique token (e.g., for email verification).
        newProfile.setActivationToken(UUID.randomUUID().toString());
//        newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));
        newProfile=profileRepository.save(newProfile);

//        send activation email
        String activationLink="http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject="Activate your Money Manager account";
        String body="Click on the following link to activate your account: "+activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);

    }
    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt((profileDTO.getCreatedAt()))
                .updatedAt((profileDTO.getUpdatedAt()))
                .build();
    }

    //Entities may contain sensitive data (e.g., passwords, tokens, internal flags).
//When sending a response back to the frontend, you don’t want to accidentally expose those fields.
//DTOs let you control exactly what gets shared.
    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt((profileEntity.getCreatedAt()))
                .updatedAt((profileEntity.getUpdatedAt()))
                .build();
    }

//    Validate token
//    This method is meant to activate a user's profile based on a token sent to their email (typically in a verification link). It's a common practice in email-based account verification systems.

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile->{
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

//    findByEmail(email) returns an Optional<ProfileEntity>.
//.map(ProfileEntity::getIsActive) gets the isActive field if the profile is found.
//If not found → orElse(false) returns false.
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

//    SecurityContextHolder holds the authentication object of the current user.
//authentication.getName() typically returns the user's email (from UserDetails).
//The profile is then fetched from the DB using that email.
//If not found, throws an exception.
    public ProfileEntity getCurrentProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("Profile not found with email "+authentication.getName()));
    }

//    If email == null, it returns the current logged-in user’s profile.
//If email is given, it fetches that user.
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser=null;
        if(email==null){
            currentUser=getCurrentProfile();
        }else{
            currentUser=profileRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("Profile not found with email "+email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt((currentUser.getCreatedAt()))
                .updatedAt((currentUser.getUpdatedAt()))
                .build();
    }


//Authenticates a user using email & password.
//If successful, it:
//Generates a JWT token (currently mocked)
//Returns a response map containing the token and user profile data.
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
//            Generate JWT token
            String token=jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }


}
