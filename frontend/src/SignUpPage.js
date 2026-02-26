import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function SignUpPage() {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [passwordCheck, setPasswordCheck] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');

  const navigate = useNavigate();

  const handleSignUp = () => {
    // 1. [추가] 프론트엔드 유효성 검증
    //    - 각 state 변수가 비어있는지 확인합니다.
    //    - trim()을 사용하여 사용자가 공백만 입력한 경우도 방지합니다.
    if (!loginId.trim() || !password.trim() || !name.trim() || !email.trim()) {
      alert('모든 항목을 입력해주세요.');
      return; // 하나라도 비어있으면 함수를 여기서 종료합니다.
    }

    if (password !== passwordCheck) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const signUpData = {
      loginId,
      password,
      name,
      email,
    };

    fetch('/api/members/signup', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(signUpData),
    })
      .then(response => {
        if (response.status === 201) {
          alert('회원가입 성공! 로그인 페이지로 이동합니다.');
          navigate('/login');
        } else {
          alert('회원가입에 실패했습니다. 입력 정보를 확인해주세요.');
        }
      })
      .catch(error => {
        console.error('회원가입 에러:', error);
        alert('회원가입 중 오류가 발생했습니다.');
      });
  };

  return (
    <div>
      <h1>회원가입</h1>
      <div>
        <input
          placeholder="아이디"
          value={loginId}
          onChange={(e) => setLoginId(e.target.value)}
        />
      </div>
      <div>
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div>
        <input
          type="password"
          placeholder="비밀번호 확인"
          value={passwordCheck}
          onChange={(e) => setPasswordCheck(e.target.value)}
        />
      </div>
      <div>
        <input
          placeholder="이름"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>
      <div>
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>
      <button onClick={handleSignUp}>회원가입</button>
    </div>
  );
}

export default SignUpPage;