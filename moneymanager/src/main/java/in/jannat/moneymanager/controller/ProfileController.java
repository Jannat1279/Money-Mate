package in.jannat.moneymanager.controller;

import in.jannat.moneymanager.dto.AuthDTO;
import in.jannat.moneymanager.dto.ProfileDTO;
import in.jannat.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already exists");
        }
    }


//💡 What it actually does:
//Takes JSON input from the frontend (React or Postman) that looks like:

//{
//  "fullName": "Jannat",
//  "email": "jannat@example.com",
//  "password": "secure123"
//}

//Maps it into a Java object (ProfileDTO) using @RequestBody.
//Passes it to the service layer:

//profileService.registerProfile(profileDTO);
//This method:
//Converts it to an Entity
//Sets activation token
//Saves it to the database
//Converts it back to a DTO
//Returns a response to the client:
//return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);

//This sends:
//HTTP 201 (Created) status
//Registered profile data (without password) in JSON


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is not active, activate your account first"));
            }
            Map<String,Object> response=profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
        }
    }
//
//    @GetMapping("/test")
//    public String test(){
//        return "test successful";
//    }
}