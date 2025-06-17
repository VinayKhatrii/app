package com.book_library.app.user.services;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book_library.app.user.dtos.UserDTO;
import com.book_library.app.user.entities.UserEntity;
import com.book_library.app.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> modelMapper.map(user, UserDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    public LocalDate getMembershipEndDate() {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + currentUsername));
        return userEntity.getMembershipEndDate();
    }

    @Transactional
    public LocalDate extendMembership(int months) {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + currentUsername));

        LocalDate newEndDate = userEntity.getMembershipEndDate() != null
            ? userEntity.getMembershipEndDate().plusMonths(months)
            : LocalDate.now().plusMonths(months);

        userEntity.setMembershipEndDate(newEndDate);
        userRepository.save(userEntity);
        return newEndDate;
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return modelMapper.map(userEntity, UserDTO.class);
    }

    @Transactional
    public void registerUser(UserDTO userDTO) {

        final String username = userDTO.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("User already exists with username: " + username);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setEmail(userDTO.getEmail());
        
        userRepository.save(userEntity);
    }

}
