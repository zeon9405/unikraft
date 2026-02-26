import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = () => {
    const loginData = {
      loginId: loginId,
      password: password,
    };

    fetch('/api/members/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loginData),
    })
      .then(response => {
        if (response.ok) {
          return response.text();
        }
        throw new Error('로그인 실패');
      })
      .then(token => {
        localStorage.setItem('token', token);
        alert('로그인 성공!');
        navigate('/');
      })
      .catch(error => {
        console.error(error);
        alert('이메일 또는 비밀번호를 확인해주세요.');
      });
  };

  return (
    <div>
      <h1>로그인</h1>
      <div>
        <input
          type="email"
          placeholder="이메일"
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
      <button onClick={handleLogin}>로그인</button>
    </div>
  );
}

export default LoginPage;