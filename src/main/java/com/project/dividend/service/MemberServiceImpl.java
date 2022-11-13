package com.project.dividend.service;

import com.project.dividend.entity.MemberEntity;
import com.project.dividend.exception.impl.AlreadyExistUserException;
import com.project.dividend.exception.impl.IncorrectPasswordException;
import com.project.dividend.exception.impl.NoExistUserException;
import com.project.dividend.model.Auth;
import com.project.dividend.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberServiceImpl implements UserDetailsService, MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    @Override
    public MemberEntity register(Auth.SignUp member) {
        boolean exist = memberRepository.existsByUsername(member.getUsername());
        if(exist) {
            throw new AlreadyExistUserException();
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        var result = memberRepository.save(member.toEntity());
        return result;
    }

    @Override
    public MemberEntity authenticate(Auth.SignIn member) {
        var user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(()-> new NoExistUserException());
        if(!passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new IncorrectPasswordException();
        }

        return user;
    }
}
