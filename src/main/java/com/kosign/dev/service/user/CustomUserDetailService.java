package com.kosign.dev.service.user;

import com.kosign.dev.domain.authentication.user.User;
import com.kosign.dev.domain.authentication.user.UserRepository;
import com.kosign.dev.exception.CustomMessageException;
import com.kosign.dev.utils.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.customUserDetail(username);
    }
    private CustomUserDetail customUserDetail(String username){
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
        if(user.isEmpty()){
            log.warn("User {} unauthorized", username);
            throw new CustomMessageException("Unauthorized",
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        }
        return new CustomUserDetail(
                user.get().getUsername(),
                user.get().getPassword(),
                user.get().getRoles()
                        .stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    public void saveUserAttemptAuthentication(String username){
        Optional<User> userOptional = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
        if (userOptional.isPresent()){
            int attempt = userOptional.get().getLoginAttempt()+1;
            userOptional.get().setLoginAttempt(attempt);
            userOptional.get().setUpdated(LocalDateTime.now());
            if (userOptional.get().getLoginAttempt() > 3){
                log.warn("Username {} update to blocked status", username);
                userOptional.get().setStatus(Constant.BLK);

            }
            userRepository.save(userOptional.get());
        }
    }
    public void updateLoginAttempt(String username){
        Optional<User> userOptional = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
        if (userOptional.isPresent()){
            userOptional.get().setLoginAttempt(0);
            userOptional.get().setUpdated(LocalDateTime.now());
            userRepository.save(userOptional.get());
        }
    }
}
