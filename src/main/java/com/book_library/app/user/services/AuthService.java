package com.book_library.app.user.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book_library.app.core.services.JwtUtil;
import com.book_library.app.user.dtos.UserDTO;
import com.book_library.app.user.dtos.UserLoginDTO;
import com.book_library.app.user.dtos.UserLoginResponseDTO;
import com.book_library.app.user.dtos.UserRegisterDTO;
import com.book_library.app.user.entities.UserEntity;
import com.book_library.app.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    
    @Transactional(readOnly = true)
    public UserLoginResponseDTO loginUser(UserLoginDTO userLoginDTO) {

        UserEntity user = userRepository.findByUsername(userLoginDTO.username())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(userLoginDTO.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new UserLoginResponseDTO(token);

    }
    
    @Transactional(readOnly = true)
    public UserLoginResponseDTO registerUser(UserRegisterDTO registerDTO) {
        
        log.info("Registering user: {}", registerDTO);
        
        if(userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("User already exists with this username");
        }
        if(userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("User already exists with this email");
        }
        registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        UserEntity user = modelMapper.map(registerDTO, UserEntity.class);
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new UserLoginResponseDTO(token);

    }
}
