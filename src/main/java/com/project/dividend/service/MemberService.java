package com.project.dividend.service;

import com.project.dividend.entity.MemberEntity;
import com.project.dividend.model.Auth;

public interface MemberService {
    MemberEntity register (Auth.SignUp member);
    MemberEntity authenticate(Auth.SignIn member);
}
